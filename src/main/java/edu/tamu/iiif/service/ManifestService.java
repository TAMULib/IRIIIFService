package edu.tamu.iiif.service;

import java.io.IOException;
import java.net.URISyntaxException;

public interface ManifestService {

    public String getManifest(String path, boolean update) throws IOException, URISyntaxException;

}
