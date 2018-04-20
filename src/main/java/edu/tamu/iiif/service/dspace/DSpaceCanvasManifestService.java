package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.ManifestType.CANVAS;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import de.digitalcollections.iiif.presentation.model.api.v2.Canvas;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.rdf.RdfResource;

@Service
public class DSpaceCanvasManifestService extends AbstractDSpaceManifestService {

    public String generateManifest(String path) throws IOException, URISyntaxException {
        String handle = extractHandle(path);
        RdfResource rdfResource = getDSpaceRdfModel(handle);
        String url = rdfResource.getResource().getURI();
        Canvas canvas = generateCanvas(new RdfResource(rdfResource, url.replace("rdf/handle", dspaceWebapp + "/bitstream").replaceAll(handle, path)));
        return mapper.writeValueAsString(canvas);
    }

    private String extractHandle(String path) {
        String[] parts = path.split("/");
        return parts[0] + "/" + parts[1];
    }

    @Override
    protected ManifestType getManifestType() {
        return CANVAS;
    }

}
