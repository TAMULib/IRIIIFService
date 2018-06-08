package edu.tamu.iiif.service;

import java.io.IOException;
import java.net.URISyntaxException;

import edu.tamu.iiif.controller.ManifestRequest;

public interface ManifestService {

    public String getManifest(ManifestRequest request) throws IOException, URISyntaxException;

}
