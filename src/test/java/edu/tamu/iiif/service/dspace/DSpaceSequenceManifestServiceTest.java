package edu.tamu.iiif.service.dspace;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class DSpaceSequenceManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpaceSequenceManifestService dSpaceSequenceManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(dSpaceSequenceManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceSequenceManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceSequenceManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(dSpaceSequenceManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dSpaceSequenceManifestService, "dspaceWebapp", DSPACE_WEBAPP);
    }

    @Test
    public void testGetManifest() {

    }

}
