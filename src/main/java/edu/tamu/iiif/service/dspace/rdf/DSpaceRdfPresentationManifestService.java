package edu.tamu.iiif.service.dspace.rdf;

import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_COLLECTION_PREDICATE;
import static edu.tamu.iiif.constants.Constants.DSPACE_HAS_ITEM_PREDICATE;
import static edu.tamu.iiif.model.ManifestType.PRESENTATION;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.NodeIterator;
import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Manifest;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.api.v2.Thumbnail;
import de.digitalcollections.iiif.presentation.model.impl.v2.ManifestImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class DSpaceRdfPresentationManifestService extends AbstractDSpaceRdfManifestService {

    public String generateManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String context = request.getContext();

        RdfResource rdfResource = getRdfResource(context);

        URI id = buildId(context);

        PropertyValueSimpleImpl label = getTitle(rdfResource);

        Manifest manifest = new ManifestImpl(id, label);

        manifest.setDescription(getDescription(rdfResource));

        List<Sequence> sequences = getSequences(request, rdfResource);

        manifest.setSequences(sequences);

        manifest.setLogo(getLogo(rdfResource));

        List<Metadata> metadata = getDublinCoreMetadata(rdfResource);

        metadata.addAll(getDublinCoreTermsMetadata(rdfResource));

        manifest.setMetadata(metadata);

        Optional<Thumbnail> thumbnail = getThumbnail(sequences);
        if (thumbnail.isPresent()) {
            manifest.setThumbnail(thumbnail.get());
        }

        Optional<String> license = getLicense(rdfResource);
        if (license.isPresent()) {
            manifest.setLicense(license.get());
        }

        return mapper.writeValueAsString(manifest);
    }

    private List<Sequence> getSequences(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Sequence> sequences = new ArrayList<Sequence>();

        // NOTE: flattening all sequence canvases into a single canvas, feature parity with Fedora presentation
        List<Sequence> broadSequences = aggregateSequences(request, rdfResource);

        if (broadSequences.size() > 0) {
            Sequence sequence = broadSequences.get(0);

            List<Canvas> canvases = sequence.getCanvases();

            broadSequences.remove(0);

            broadSequences.forEach(seq -> {
                seq.getCanvases().forEach(canvas -> {
                    canvases.add(canvas);
                });
            });

            sequence.setCanvases(canvases);
            sequences.add(sequence);
        }

        return sequences;
    }

    private List<Sequence> aggregateSequences(ManifestRequest request, RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Sequence> sequences = new ArrayList<Sequence>();
        if (isTopLevelCommunity(rdfResource.getModel()) || isSubcommunity(rdfResource.getModel())) {
            sequences.addAll(getSequencesByPredicate(request, rdfResource, DSPACE_HAS_COLLECTION_PREDICATE));
        } else if (isCollection(rdfResource.getModel())) {
            sequences.addAll(getSequencesByPredicate(request, rdfResource, DSPACE_HAS_ITEM_PREDICATE));
        } else if (isItem(rdfResource.getModel())) {
            sequences.add(generateSequence(request, rdfResource));
        }
        return sequences;
    }

    private List<Sequence> getSequencesByPredicate(ManifestRequest request, RdfResource rdfResource, String predicate) throws NotFoundException, IOException, URISyntaxException {
        List<Sequence> sequences = new ArrayList<Sequence>();
        NodeIterator nodeIterator = rdfResource.getAllNodesOfPropertyWithId(predicate);
        while (nodeIterator.hasNext()) {
            String uri = nodeIterator.next().toString();
            String handle = getHandle(uri);
            sequences.addAll(aggregateSequences(request, getRdfResource(handle)));
        }
        return sequences;
    }

    @Override
    protected ManifestType getManifestType() {
        return PRESENTATION;
    }

}
