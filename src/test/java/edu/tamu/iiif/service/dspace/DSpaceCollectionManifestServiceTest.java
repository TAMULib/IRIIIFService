package edu.tamu.iiif.service.dspace;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class DSpaceCollectionManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpaceCollectionManifestService dSpaceCollectionManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "dspaceWebapp", DSPACE_WEBAPP);
    }

    @Test
    public void testGetManifest() {

    }

}
