package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.ManifestType.SEQUENCE;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class DSpaceSequenceManifestService extends AbstractDSpaceManifestService {

    public String generateManifest(String handle) throws IOException, URISyntaxException {

        RdfResource rdfResource = getDSpaceRdfModel(handle);

        Sequence sequence = generateSequence(rdfResource);

        sequence.setDescription(getDescription(rdfResource));

        return mapper.writeValueAsString(sequence);
    }

    @Override
    protected ManifestType getManifestType() {
        return SEQUENCE;
    }

}
