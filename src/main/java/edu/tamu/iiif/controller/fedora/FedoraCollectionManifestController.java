package edu.tamu.iiif.controller.fedora;

import static edu.tamu.iiif.constants.Constants.COLLECECTION_IDENTIFIER;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import edu.tamu.iiif.annotation.ContextIdentifier;
import edu.tamu.iiif.annotation.ManifestEndpoint;
import edu.tamu.iiif.controller.AbstractManifestController;
import edu.tamu.iiif.controller.ManifestBuilder;
import edu.tamu.iiif.service.fedora.FedoraCollectionManifestService;

//@formatter:off
@ManifestEndpoint(
    path = "/${iiif.fedora.identifier.fedora-pcdm}",
    condition = "'${spring.profiles.include}'.contains('${iiif.fedora.identifier.fedora-pcdm}')"
)
public class FedoraCollectionManifestController extends AbstractManifestController<FedoraCollectionManifestService> {

    @GetMapping("/" + COLLECECTION_IDENTIFIER + "/**/*")
    public void manifest(
        HttpServletResponse response,
        @ContextIdentifier String path,
        @RequestParam(value = "update", required = false, defaultValue = "false") boolean update,
        @RequestParam(value = "allow", required = false, defaultValue = "") List<String> allowed,
        @RequestParam(value = "disallow", required = false, defaultValue = "") List<String> disallowed
    ) throws IOException, URISyntaxException {
        sendManifest(ManifestBuilder.build(response, path, update, allowed, disallowed));
    }

}
//@formatter:on