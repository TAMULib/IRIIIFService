package edu.tamu.iiif.service.fedora.pcdm.image;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmImageManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class FedoraPcdmImageManifestServiceUncodedTest extends AbstractImageValid {

    @InjectMocks
    private FedoraPcdmImageManifestService fedoraPcdmImageManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmImageManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat0084";
    }

    protected FedoraPcdmImageManifestService getManifestService() {
        return fedoraPcdmImageManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "uncoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        restGetRdfSuccess(IMAGE_SERVICE_URL_PATH + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0MDA4NA==", image);
    }

}
