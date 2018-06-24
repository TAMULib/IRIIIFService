package edu.tamu.iiif.controller.fedora.pcdm;

import static edu.tamu.iiif.constants.Constants.FEDORA_PCDM_CONDITION;
import static edu.tamu.iiif.constants.Constants.FEDORA_PCDM_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IMAGE_MAPPING;
import static edu.tamu.iiif.controller.ManifestBuilder.build;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;

import edu.tamu.iiif.annotation.ManifestController;
import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmImageManifestService;

@ManifestController(path = FEDORA_PCDM_IDENTIFIER, condition = FEDORA_PCDM_CONDITION)
public class FedoraPcdmImageManifestController extends AbstractManifestController<FedoraPcdmImageManifestService> {

    @GetMapping(IMAGE_MAPPING)
    public void manifest(HttpServletResponse response, ManifestRequest request) throws IOException, URISyntaxException {
        sendManifest(build(response, request));
    }

}
