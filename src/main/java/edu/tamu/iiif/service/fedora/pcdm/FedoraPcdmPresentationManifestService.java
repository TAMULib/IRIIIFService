package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Manifest;
import de.digitalcollections.iiif.presentation.model.api.v2.Metadata;
import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import de.digitalcollections.iiif.presentation.model.api.v2.Thumbnail;
import de.digitalcollections.iiif.presentation.model.impl.v2.ManifestImpl;
import de.digitalcollections.iiif.presentation.model.impl.v2.PropertyValueSimpleImpl;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;
import edu.tamu.iiif.utility.RdfModelUtility;

@Service
public class FedoraPcdmPresentationManifestService extends AbstractFedoraPcdmManifestService {

    public String generateManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String context = request.getContext();

        String parameterizedContext = RdfModelUtility.getParameterizedId(request);

        RdfResource rdfResource = getRdfResourceByContextPath(context);

        URI id = buildId(parameterizedContext);

        boolean isCollection = isCollection(rdfResource);

        if (isCollection) {
            // if container is a collection have to use objects container for rdf resource
            // as it contains metadata and iana proxies
            String collectionObjectMemberId = getCollectionObjectsMember(rdfResource);
            Model collectionObjectMemberModel = getFedoraRdfModel(collectionObjectMemberId);
            rdfResource = new RdfResource(collectionObjectMemberModel, collectionObjectMemberId);
        }

        Manifest manifest = new ManifestImpl(id, getLabel(rdfResource));

        List<Metadata> metadata = getMetadata(rdfResource);

        if (!metadata.isEmpty()) {
            manifest.setMetadata(metadata);
        }

        List<Sequence> sequences = getSequences(request, rdfResource);

        manifest.setSequences(sequences);

        manifest.setLogo(getLogo(rdfResource));

        Optional<PropertyValueSimpleImpl> description = getDescription(rdfResource);
        if (description.isPresent()) {
            manifest.setDescription(description.get());
        }

        Optional<PropertyValueSimpleImpl> attribution = getAttribution(rdfResource);
        if (attribution.isPresent()) {
            manifest.setAttribution(attribution.get());
        }

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

        sequences.add(generateSequence(request, rdfResource));

        return sequences;
    }

    @Override
    public ManifestType getManifestType() {
        return PRESENTATION;
    }

}
