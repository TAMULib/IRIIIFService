package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.model.ManifestType.SEQUENCE;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Sequence;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class FedoraPcdmSequenceManifestService extends AbstractFedoraPcdmManifestService {

    public String generateManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String context = request.getContext();
        RdfResource rdfResource = getRdfResource(context);
        Sequence senquence = generateSequence(request, rdfResource);
        return mapper.writeValueAsString(senquence);
    }

    @Override
    protected ManifestType getManifestType() {
        return SEQUENCE;
    }

}
