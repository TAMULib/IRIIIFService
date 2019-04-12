package edu.tamu.iiif.service.dspace.rdf;

import static edu.tamu.iiif.model.ManifestType.IMAGE;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;

@Service
public class DSpaceRdfImageManifestService extends AbstractDSpaceRdfManifestService {

    public String generateManifest(ManifestRequest request) throws IOException, URISyntaxException {
        String context = request.getContext();
        String dspacePath = config.getWebapp() != null && config.getWebapp().length() > 0 ? joinPath(config.getUrl(), config.getWebapp(), "bitstream", context) : joinPath(config.getUrl(), "bitstream", context);
        URI uri = getImageUri(dspacePath);
        return fetchImageInfo(uri.toString());
    }

    @Override
    protected ManifestType getManifestType() {
        return IMAGE;
    }

}
