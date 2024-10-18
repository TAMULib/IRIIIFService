package edu.tamu.iiif.service.fedora.pcdm.sequence;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmSequenceManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class FedoraPcdmSequenceManifestServiceDecodedTest extends AbstractSequenceInvalidSpace {

    @InjectMocks
    private FedoraPcdmSequenceManifestService fedoraPcdmSequenceManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmSequenceManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat 0084";
    }

    protected FedoraPcdmSequenceManifestService getManifestService() {
        return fedoraPcdmSequenceManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "decoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        // No mocks needed.
    }

}
