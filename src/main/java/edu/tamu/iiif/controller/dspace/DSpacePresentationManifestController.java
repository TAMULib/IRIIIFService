package edu.tamu.iiif.controller.dspace;

import static edu.tamu.iiif.constants.rdf.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.PRESENTATION_IDENTIFIER;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.service.dspace.DSpacePresentationManifestService;

@RestController
@RequestMapping("/" + DSPACE_IDENTIFIER)
public class DSpacePresentationManifestController extends AbstractManifestController<DSpacePresentationManifestService> {

    @RequestMapping("/" + PRESENTATION_IDENTIFIER)
    public void presentation(HttpServletResponse response, @RequestParam(value = "path", required = true) String path, @RequestParam(value = "update", required = false, defaultValue = "false") boolean update) throws IOException, URISyntaxException {
        sendManifest(response, path, update);
    }

}
