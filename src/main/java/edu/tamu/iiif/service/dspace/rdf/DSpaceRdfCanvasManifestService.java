package edu.tamu.iiif.service.dspace.rdf;

import static edu.tamu.iiif.model.ManifestType.CANVAS;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.CanvasWithInfo;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class DSpaceRdfCanvasManifestService extends AbstractDSpaceRdfManifestService {

    public String generateManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String context = request.getContext();
        String handle = extractHandle(context);
        RdfResource rdfResource = getRdfResourceByContextPath(handle);
        String uri = rdfResource.getResource().getURI()
            .replace("rdf/handle", config.getWebapp() != null && config.getWebapp().length() > 0 ? config.getWebapp() + "/bitstream" : "bitstream")
            .replaceAll(handle, context);

        CanvasWithInfo canvasWithInfo = generateCanvas(request, new RdfResource(rdfResource, uri), 0);
        return mapper.writeValueAsString(canvasWithInfo.getCanvas());
    }

    private String extractHandle(String context) {
        String[] parts = context.split("/");
        return parts[0] + "/" + parts[1];
    }

    @Override
    public ManifestType getManifestType() {
        return CANVAS;
    }

}
