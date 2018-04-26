package edu.tamu.iiif.service.dspace;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class DSpaceCanvasManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpaceCanvasManifestService dSpaceCanvasManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(dSpaceCanvasManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceCanvasManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceCanvasManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(dSpaceCanvasManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dSpaceCanvasManifestService, "dspaceWebapp", DSPACE_WEBAPP);
    }

    @Test
    public void testGetManifest() {

    }

}
