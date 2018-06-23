package edu.tamu.iiif.service.fedora;

import org.springframework.test.util.ReflectionTestUtils;

import edu.tamu.iiif.service.AbstractManifestServiceTest;

public abstract class AbstractFedoraManifestServiceTest extends AbstractManifestServiceTest {

    protected static final String FEDORA_URL = "http://localhost:9000/fcrepo/rest";

    protected static final String PCDM_RDF_URL = "http://localhost:9107/pcdm";

    protected void setup(AbstractFedoraManifestService fedoraManifestService) {
        super.setup(fedoraManifestService);
        ReflectionTestUtils.setField(fedoraManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraManifestService, "fedoraPcdmExtUrl", PCDM_RDF_URL);
    }

}
