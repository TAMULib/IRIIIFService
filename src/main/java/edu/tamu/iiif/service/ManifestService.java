package edu.tamu.iiif.service;

import java.io.IOException;
import java.net.URISyntaxException;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;

public interface ManifestService {

    public String getManifest(ManifestRequest request) throws IOException, URISyntaxException;

    public String getRepository();

    public ManifestType getManifestType();

}
