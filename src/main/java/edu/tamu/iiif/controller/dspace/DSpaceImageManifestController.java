package edu.tamu.iiif.controller.dspace;

import static edu.tamu.iiif.constants.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.iiif.annotation.Context;
import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.controller.ManifestBuilder;
import edu.tamu.iiif.service.dspace.DSpaceImageManifestService;

@RestController
@Profile(DSPACE_IDENTIFIER)
@RequestMapping("/" + DSPACE_IDENTIFIER)
public class DSpaceImageManifestController extends AbstractManifestController<DSpaceImageManifestService> {

    @RequestMapping("/" + IMAGE_IDENTIFIER + "/**/*")
    public void image(
        // @formatter:off
        HttpServletResponse response,
        @Context String path,
        @RequestParam(value = "update", required = false, defaultValue = "false") boolean update,
        @RequestParam(value = "allow", required = false, defaultValue = "") List<String> allowed,
        @RequestParam(value = "disallow", required = false, defaultValue = "") List<String> disallowed
        // @formatter:on
    ) throws IOException, URISyntaxException {
        sendManifest(ManifestBuilder.of(response, path, update, allowed, disallowed));
    }

}
