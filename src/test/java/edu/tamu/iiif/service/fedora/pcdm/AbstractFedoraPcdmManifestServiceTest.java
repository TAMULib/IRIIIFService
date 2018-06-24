package edu.tamu.iiif.service.fedora.pcdm;

import static org.springframework.test.util.ReflectionTestUtils.setField;

import edu.tamu.iiif.service.AbstractManifestServiceTest;
import edu.tamu.iiif.service.fedora.pcdm.AbstractFedoraPcdmManifestService;

public abstract class AbstractFedoraPcdmManifestServiceTest extends AbstractManifestServiceTest {

    protected static final String FEDORA_URL = "http://localhost:9000/fcrepo/rest";

    protected static final String PCDM_RDF_URL = "http://localhost:9107/pcdm";

    protected static final String FEDORA_PCDM_IDENTIFIER = "fedora-pcdm";

    protected void setup(AbstractFedoraPcdmManifestService fedoraPcdmManifestService) {
        super.setup(fedoraPcdmManifestService);
        setField(fedoraPcdmManifestService, "fedoraUrl", FEDORA_URL);
        setField(fedoraPcdmManifestService, "fedoraPcdmExtUrl", PCDM_RDF_URL);
        setField(fedoraPcdmManifestService, "fedoraPcdmIdentifier", FEDORA_PCDM_IDENTIFIER);
    }

}
