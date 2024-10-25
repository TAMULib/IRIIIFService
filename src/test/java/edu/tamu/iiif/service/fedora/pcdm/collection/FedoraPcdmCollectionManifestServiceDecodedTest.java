package edu.tamu.iiif.service.fedora.pcdm.collection;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmCollectionManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class FedoraPcdmCollectionManifestServiceDecodedTest extends AbstractCollectionInvalidSpace {

    @InjectMocks
    private FedoraPcdmCollectionManifestService fedoraPcdmCollectionManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmCollectionManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat 0084";
    }

    protected FedoraPcdmCollectionManifestService getManifestService() {
        return fedoraPcdmCollectionManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "decoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        restGetRdfBadRequest(FEDORA_URL_PATH + "/" + getManifestCollectionPath());
    }

}
