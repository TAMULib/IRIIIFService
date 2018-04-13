package edu.tamu.iiif.service.fedora;

import static edu.tamu.iiif.model.ManifestType.IMAGE;
import static edu.tamu.iiif.utility.StringUtility.joinPath;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import edu.tamu.iiif.model.ManifestType;

@Service
public class FedoraImageManifestService extends AbstractFedoraManifestService {

    public String generateManifest(String path) throws IOException, URISyntaxException {
        String fedoraPath = joinPath(fedoraUrl, path);
        URI uri = getImageUri(fedoraPath);
        return fetchImageInfo(uri.toString());
    }

    @Override
    protected ManifestType getManifestType() {
        return IMAGE;
    }

}
