package edu.tamu.iiif.service.fedora;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class FedoraImageManifestServiceTest extends AbstractFedoraManifestServiceTest {

    @InjectMocks
    private FedoraImageManifestService fedoraImageManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(fedoraImageManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraImageManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraImageManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(fedoraImageManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraImageManifestService, "pcdmRdfExtUrl", PCDM_RDF_URL);
    }

    @Test
    public void testGetManifest() {

    }

}
