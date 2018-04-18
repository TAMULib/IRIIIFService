package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.model.ManifestType.SEQUENCE;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class FedoraSequenceManifestService extends AbstractFedoraManifestService {

    public String generateManifest(String path) throws IOException, URISyntaxException {
        RdfResource rdfResource = getRdfResource(path);
        Sequence senquence = generateSequence(rdfResource);
        return mapper.writeValueAsString(senquence);
    }

    @Override
    protected ManifestType getManifestType() {
        return SEQUENCE;
    }

}
