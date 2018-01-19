package edu.tamu.iiif.controller.fedora;

import static edu.tamu.iiif.constants.rdf.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.FEDORA_IDENTIFIER;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.service.fedora.FedoraCollectionManifestService;

@RestController
@RequestMapping("/" + FEDORA_IDENTIFIER)
public class FedoraCollectionManifestController extends AbstractManifestController<FedoraCollectionManifestService> {

    @RequestMapping("/" + COLLECECTION_IDENTIFIER)
    public void collection(HttpServletResponse response, @RequestParam(value = "path", required = true) String path, @RequestParam(value = "update", required = false, defaultValue = "false") boolean update) throws IOException, URISyntaxException {
        sendManifest(response, path, update);
    }

}
