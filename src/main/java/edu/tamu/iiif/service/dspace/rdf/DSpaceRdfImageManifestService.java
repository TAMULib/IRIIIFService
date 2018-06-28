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
        String dspacePath = dspaceWebapp != null && dspaceWebapp.length() > 0 ? joinPath(dspaceUrl, dspaceWebapp, "bitstream", context) : joinPath(dspaceUrl, "bitstream", context);
        URI uri = getImageUri(dspacePath);
        return fetchImageInfo(uri.toString());
    }

    @Override
    protected ManifestType getManifestType() {
        return IMAGE;
    }

}
