package edu.tamu.iiif.controller.fedora;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.controller.ManifestBuilder.build;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;

import edu.tamu.iiif.annotation.ManifestController;
import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.fedora.FedoraPcdmCanvasManifestService;

//@formatter:off
@ManifestController(
    path = "/${iiif.fedora.identifier.fedora-pcdm}",
    condition = "'${spring.profiles.include}'.contains('${iiif.fedora.identifier.fedora-pcdm}')"
)
public class FedoraPcdmCanvasManifestController extends AbstractManifestController<FedoraPcdmCanvasManifestService> {

    @GetMapping("/" + CANVAS_IDENTIFIER + "/**/*")
    public void manifest(HttpServletResponse response, ManifestRequest request) throws IOException, URISyntaxException {
        sendManifest(build(response, request));
    }

}
//@formatter:on
