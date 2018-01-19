package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.RepositoryType.DSPACE;

import org.springframework.beans.factory.annotation.Value;

import edu.tamu.iiif.model.RepositoryType;
import edu.tamu.iiif.service.AbstractManifestService;

public abstract class AbstractDSpaceManifestService extends AbstractManifestService {

    @Value("${iiif.dspace.url}")
    protected String dspaceUrl;

    @Override
    protected RepositoryType getRepositoryType() {
        return DSPACE;
    }

}
