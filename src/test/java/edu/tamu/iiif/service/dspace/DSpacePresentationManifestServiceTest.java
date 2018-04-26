package edu.tamu.iiif.service.dspace;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class DSpacePresentationManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpacePresentationManifestService dSpacePresentationManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(dSpacePresentationManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(dSpacePresentationManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(dSpacePresentationManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(dSpacePresentationManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dSpacePresentationManifestService, "dspaceWebapp", DSPACE_WEBAPP);
    }

    @Test
    public void testGetManifest() {

    }

}
