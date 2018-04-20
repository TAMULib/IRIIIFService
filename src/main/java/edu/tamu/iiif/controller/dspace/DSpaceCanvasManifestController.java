package edu.tamu.iiif.controller.dspace;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.DSPACE_IDENTIFIER;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.service.dspace.DSpaceCanvasManifestService;

@RestController
@Profile(DSPACE_IDENTIFIER)
@RequestMapping("/" + DSPACE_IDENTIFIER)
public class DSpaceCanvasManifestController extends AbstractManifestController<DSpaceCanvasManifestService> {

    @RequestMapping("/" + CANVAS_IDENTIFIER)
    public void image(HttpServletResponse response, @RequestParam(value = "path", required = true) String path, @RequestParam(value = "update", required = false, defaultValue = "false") boolean update) throws IOException, URISyntaxException {
        sendManifest(response, path, update);
    }

}
