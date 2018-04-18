package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.ManifestType.IMAGE;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import edu.tamu.iiif.model.ManifestType;

@Service
public class DSpaceImageManifestService extends AbstractDSpaceManifestService {

    public String generateManifest(String path) throws IOException, URISyntaxException {
        String dspacePath = joinPath(dspaceUrl, "xmlui", "bitstream", path);
        System.out.println("\n\n" + dspacePath + "\n\n");
        URI uri = getImageUri(dspacePath);
        System.out.println("\n\n" + uri.toString() + "\n\n");
        return fetchImageInfo(uri.toString());
    }

    @Override
    protected ManifestType getManifestType() {
        return IMAGE;
    }

}
