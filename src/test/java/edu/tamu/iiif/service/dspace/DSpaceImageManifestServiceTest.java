package edu.tamu.iiif.service.dspace;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class DSpaceImageManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpaceImageManifestService dSpaceImageManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(dSpaceImageManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceImageManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceImageManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(dSpaceImageManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dSpaceImageManifestService, "dspaceWebapp", DSPACE_WEBAPP);
    }

    @Test
    public void testGetManifest() {

    }

}
