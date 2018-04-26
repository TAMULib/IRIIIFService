package edu.tamu.iiif.service.fedora;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class FedoraPresentationManifestServiceTest extends AbstractFedoraManifestServiceTest {

    @InjectMocks
    private FedoraPresentationManifestService fedoraPresentationManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(fedoraPresentationManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraPresentationManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraPresentationManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(fedoraPresentationManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraPresentationManifestService, "pcdmRdfExtUrl", PCDM_RDF_URL);
    }

    @Test
    public void testGetManifest() {

    }

}
