package edu.tamu.iiif.service.fedora;

import org.springframework.test.util.ReflectionTestUtils;

import edu.tamu.iiif.service.AbstractManifestServiceTest;

public abstract class AbstractFedoraPcdmManifestServiceTest extends AbstractManifestServiceTest {

    protected static final String FEDORA_URL = "http://localhost:9000/fcrepo/rest";

    protected static final String PCDM_RDF_URL = "http://localhost:9107/pcdm";

    protected static final String FEDORA_PCDM_IDENTIFIER = "fedora-pcdm";

    protected void setup(AbstractFedoraPcdmManifestService fedoraPcdmManifestService) {
        super.setup(fedoraPcdmManifestService);
        ReflectionTestUtils.setField(fedoraPcdmManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraPcdmManifestService, "fedoraPcdmExtUrl", PCDM_RDF_URL);
        ReflectionTestUtils.setField(fedoraPcdmManifestService, "fedoraPcdmIdentifier", FEDORA_PCDM_IDENTIFIER);
    }

}
