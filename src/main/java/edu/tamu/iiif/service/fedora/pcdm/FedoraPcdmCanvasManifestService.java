package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.model.ManifestType.CANVAS;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.CanvasWithInfo;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class FedoraPcdmCanvasManifestService extends AbstractFedoraPcdmManifestService {

    public String generateManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String context = request.getContext();
        RdfResource rdfResource = getRdfResourceByContextPath(context);
        CanvasWithInfo canvasWithInfo = generateCanvas(request, rdfResource, 0);
        return mapper.writeValueAsString(canvasWithInfo.getCanvas());
    }

    @Override
    public ManifestType getManifestType() {
        return CANVAS;
    }

}
