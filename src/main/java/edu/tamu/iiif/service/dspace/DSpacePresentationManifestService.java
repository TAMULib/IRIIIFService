package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import de.digitalcollections.iiif.presentation.model.api.v2.Image;
import de.digitalcollections.iiif.presentation.model.api.v2.Manifest;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.api.v2.Thumbnail;
import de.digitalcollections.iiif.presentation.model.impl.v2.ManifestImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.SequenceImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.ThumbnailImpl;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class DSpacePresentationManifestService extends AbstractDSpaceManifestService {

    public String generateManifest(String handle) throws IOException, URISyntaxException {

        RdfResource rdfResource = getDSpaceRdfModel(handle);

        URI id = buildId(handle);

        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(handle);

        Manifest manifest = new ManifestImpl(id, label);

        List<Sequence> sequences = getSequences(rdfResource);

        manifest.setSequences(sequences);

        manifest.setLogo(getLogo(rdfResource));

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

    private List<Sequence> getSequences(RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Sequence> sequences = new ArrayList<Sequence>();

        sequences.add(generateSequence(rdfResource));

        return sequences;
    }

    private Sequence generateSequence(RdfResource rdfResource) throws IOException, URISyntaxException {
        String id = rdfResource.getResource().getURI();
        PropertyValueSimpleImpl label = new PropertyValueSimpleImpl(formalize(extractLabel(id)));
        Sequence sequence = new SequenceImpl(getDSpaceIIIFPresentationUri(id), label);
        sequence.setCanvases(getCanvases(rdfResource));
        return sequence;
    }

    private List<Canvas> getCanvases(RdfResource rdfResource) throws IOException, URISyntaxException {
        List<Canvas> canvases = new ArrayList<Canvas>();

        return canvases;
    }

    private Optional<Thumbnail> getThumbnail(List<Sequence> sequences) throws URISyntaxException {
        for (Sequence sequence : sequences) {
            for (Canvas canvas : sequence.getCanvases()) {
                for (Image image : canvas.getImages()) {
                    if (Optional.ofNullable(image.getResource()).isPresent()) {
                        URI serviceURI = image.getResource().getServices().get(0).getId();
                        Thumbnail thubmnail = new ThumbnailImpl(serviceUrlToThumbnailUri(serviceURI));
                        thubmnail.setServices(image.getResource().getServices());
                        return Optional.of(thubmnail);
                    }
                }
            }
        }
        return Optional.empty();
    }

    @Override
    protected ManifestType getManifestType() {
        return PRESENTATION;
    }

}
