package edu.tamu.iiif.controller.fedora;

import static edu.tamu.iiif.constants.Constants.FEDORA_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Profile;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.service.fedora.FedoraImageManifestService;

@RestController
@Profile(FEDORA_IDENTIFIER)
@RequestMapping("/" + FEDORA_IDENTIFIER)
public class FedoraImageManifestController extends AbstractManifestController<FedoraImageManifestService> {

    @RequestMapping("/" + IMAGE_IDENTIFIER)
    public void image(HttpServletResponse response, @RequestParam(value = "path", required = true) String path, @RequestParam(value = "update", required = false, defaultValue = "false") boolean update) throws IOException, URISyntaxException {
        sendManifest(response, path, update);
    }

}
