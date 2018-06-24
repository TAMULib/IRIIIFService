package edu.tamu.iiif.controller.dspace;

import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.controller.ManifestBuilder.build;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;

import edu.tamu.iiif.annotation.ManifestController;
import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.dspace.DSpaceRdfImageManifestService;

//@formatter:off
@ManifestController(
    path = "/${iiif.dspace.identifier.dspace-rdf}",
    condition = "'${spring.profiles.include}'.contains('${iiif.dspace.identifier.dspace-rdf}')"
)
public class DSpaceRdfImageManifestController extends AbstractManifestController<DSpaceRdfImageManifestService> {

    @GetMapping("/" + IMAGE_IDENTIFIER + "/**/*")
    public void manifest(HttpServletResponse response, ManifestRequest request) throws IOException, URISyntaxException {
        sendManifest(build(response, request));
    }

}
//@formatter:on
