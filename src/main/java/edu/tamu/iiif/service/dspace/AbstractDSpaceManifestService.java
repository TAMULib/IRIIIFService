package edu.tamu.iiif.service.dspace;

import org.springframework.beans.factory.annotation.Value;

import edu.tamu.iiif.service.AbstractManifestService;

public abstract class AbstractDSpaceManifestService extends AbstractManifestService {

    @Value("${iiif.dspace.url}")
    protected String dspaceUrl;

}
