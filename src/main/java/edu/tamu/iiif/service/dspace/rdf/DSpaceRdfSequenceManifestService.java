package edu.tamu.iiif.service.dspace.rdf;

import static edu.tamu.iiif.model.ManifestType.SEQUENCE;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class DSpaceRdfSequenceManifestService extends AbstractDSpaceRdfManifestService {

    public String generateManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String context = request.getContext();

        RdfResource rdfResource = getRdfResource(context);

        Sequence sequence = generateSequence(request, rdfResource);

        sequence.setDescription(getDescription(rdfResource));

        return mapper.writeValueAsString(sequence);
    }

    @Override
    protected ManifestType getManifestType() {
        return SEQUENCE;
    }

}
