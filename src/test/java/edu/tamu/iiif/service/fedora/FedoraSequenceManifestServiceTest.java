package edu.tamu.iiif.service.fedora;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.test.util.ReflectionTestUtils;

public class FedoraSequenceManifestServiceTest extends AbstractFedoraManifestServiceTest {

    @InjectMocks
    private FedoraSequenceManifestService fedoraSequenceManifestService;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(fedoraSequenceManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraSequenceManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraSequenceManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(fedoraSequenceManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraSequenceManifestService, "pcdmRdfExtUrl", PCDM_RDF_URL);
    }

    @Test
    public void testGetManifest() {

    }

}
