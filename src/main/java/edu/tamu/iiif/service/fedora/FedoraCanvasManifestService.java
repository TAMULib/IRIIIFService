package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.model.ManifestType.CANVAS;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class FedoraCanvasManifestService extends AbstractFedoraManifestService {

    public String generateManifest(String path) throws IOException, URISyntaxException {
        RdfResource rdfResource = getRdfResource(path);
        Canvas canvas = generateCanvas(rdfResource);
        return mapper.writeValueAsString(canvas);
    }

    @Override
    protected ManifestType getManifestType() {
        return CANVAS;
    }

}
