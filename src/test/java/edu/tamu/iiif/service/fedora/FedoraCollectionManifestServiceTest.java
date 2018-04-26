package edu.tamu.iiif.service.fedora;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class FedoraCollectionManifestServiceTest extends AbstractFedoraManifestServiceTest {

    @InjectMocks
    private FedoraCollectionManifestService fedoraCollectionManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(fedoraCollectionManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraCollectionManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraCollectionManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(fedoraCollectionManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraCollectionManifestService, "pcdmRdfExtUrl", PCDM_RDF_URL);
    }

    @Test
    public void testGetManifest() {

    }

}
