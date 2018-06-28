package edu.tamu.iiif.service.dspace.rdf;

import static edu.tamu.iiif.model.ManifestType.CANVAS;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class DSpaceRdfCanvasManifestService extends AbstractDSpaceRdfManifestService {

    public String generateManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String context = request.getContext();
        String handle = extractHandle(context);
        RdfResource rdfResource = getRdfResource(handle);
        String url = rdfResource.getResource().getURI();
        Canvas canvas = generateCanvas(request, new RdfResource(rdfResource, url.replace("rdf/handle", dspaceWebapp != null && dspaceWebapp.length() > 0 ? dspaceWebapp + "/bitstream" : "bitstream").replaceAll(handle, context)));
        return mapper.writeValueAsString(canvas);
    }

    private String extractHandle(String context) {
        String[] parts = context.split("/");
        return parts[0] + "/" + parts[1];
    }

    @Override
    protected ManifestType getManifestType() {
        return CANVAS;
    }

}
