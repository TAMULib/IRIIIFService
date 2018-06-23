package edu.tamu.iiif.service.dspace;

import org.springframework.test.util.ReflectionTestUtils;

import edu.tamu.iiif.service.AbstractManifestServiceTest;

public abstract class AbstractDSpaceManifestServiceTest extends AbstractManifestServiceTest {

    protected static final String DSPACE_URL = "http://localhost:8080";

    protected static final String DSPACE_WEBAPP = "xmlui";

    protected void setup(AbstractDSpaceManifestService dSpaceManifestService) {
        super.setup(dSpaceManifestService);
        ReflectionTestUtils.setField(dSpaceManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dSpaceManifestService, "dspaceWebapp", DSPACE_WEBAPP);
    }

}
