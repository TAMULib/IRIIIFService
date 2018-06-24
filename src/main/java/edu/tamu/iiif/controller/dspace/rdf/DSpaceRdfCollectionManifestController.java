package edu.tamu.iiif.controller.dspace.rdf;

import static edu.tamu.iiif.constants.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.controller.ManifestBuilder.build;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;

import edu.tamu.iiif.annotation.ManifestController;
import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCollectionManifestService;

//@formatter:off
@ManifestController(
    path = "/${iiif.dspace.identifier.dspace-rdf}",
    condition = "'${spring.profiles.include}'.contains('${iiif.dspace.identifier.dspace-rdf}')"
)
public class DSpaceRdfCollectionManifestController extends AbstractManifestController<DSpaceRdfCollectionManifestService> {

    @GetMapping("/" + COLLECECTION_IDENTIFIER + "/**/*")
    public void manifest(HttpServletResponse response, ManifestRequest request) throws IOException, URISyntaxException {
        sendManifest(build(response, request));
    }

}
//@formatter:on
