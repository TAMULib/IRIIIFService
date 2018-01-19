package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;

import java.io.IOException;
import java.net.URISyntaxException;

import org.springframework.stereotype.Service;

import edu.tamu.iiif.model.ManifestType;

@Service
public class DSpacePresentationManifestService extends AbstractDSpaceManifestService {

    public String generateManifest(String path) throws IOException, URISyntaxException {
        return "{}";
    }

    @Override
    protected ManifestType getManifestType() {
        return PRESENTATION;
    }

}
