package edu.tamu.iiif.controller.dspace;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.controller.ManifestBuilder.build;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.tamu.iiif.annotation.ContextIdentifier;
import edu.tamu.iiif.annotation.ManifestEndpoint;
import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.service.dspace.DSpaceRdfCanvasManifestService;

//@formatter:off
@ManifestEndpoint(
    path = "/${iiif.dspace.identifier.dspace-rdf}",
    condition = "'${spring.profiles.include}'.contains('${iiif.dspace.identifier.dspace-rdf}')"
)
public class DSpaceRdfCanvasManifestController extends AbstractManifestController<DSpaceRdfCanvasManifestService> {

    @GetMapping("/" + CANVAS_IDENTIFIER + "/**/*")
    public void manifest(
        HttpServletResponse response,
        @ContextIdentifier String path,
        @RequestParam(value = "update", required = false, defaultValue = "false") boolean update,
        @RequestParam(value = "allow", required = false, defaultValue = "") List<String> allowed,
        @RequestParam(value = "disallow", required = false, defaultValue = "") List<String> disallowed
    ) throws IOException, URISyntaxException {
        sendManifest(build(response, path, update, allowed, disallowed));
    }

}
//@formatter:on
