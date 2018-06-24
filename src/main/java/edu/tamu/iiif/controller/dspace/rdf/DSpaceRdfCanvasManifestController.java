package edu.tamu.iiif.controller.dspace.rdf;

import static edu.tamu.iiif.constants.Constants.CANVAS_MAPPING;
import static edu.tamu.iiif.constants.Constants.DSPACE_RDF_CONDITION;
import static edu.tamu.iiif.constants.Constants.DSPACE_RDF_IDENTIFIER;
import static edu.tamu.iiif.controller.ManifestBuilder.build;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;

import edu.tamu.iiif.annotation.ManifestController;
import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCanvasManifestService;

@ManifestController(path = DSPACE_RDF_IDENTIFIER, condition = DSPACE_RDF_CONDITION)
public class DSpaceRdfCanvasManifestController extends AbstractManifestController<DSpaceRdfCanvasManifestService> {

    @GetMapping(CANVAS_MAPPING)
    public void manifest(HttpServletResponse response, ManifestRequest request) throws IOException, URISyntaxException {
        sendManifest(build(response, request));
    }

}
