package edu.tamu.iiif.service.fedora.pcdm.sequence;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmSequenceManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class FedoraPcdmSequenceManifestServiceEncodedTest extends AbstractSequenceValid {

    @InjectMocks
    private FedoraPcdmSequenceManifestService fedoraPcdmSequenceManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmSequenceManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat%200084";
    }

    protected FedoraPcdmSequenceManifestService getManifestService() {
        return fedoraPcdmSequenceManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "encoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        restGetRdfSuccess(FEDORA_URL_PATH + "/" + getManifestPagePath(), itemRdf);
        restGetRdfSuccess(FEDORA_URL_PATH + "/" + getManifestPagePath() + "/files/fcr:metadata", itemFilesRdf);
        restGetRdfSuccess(FEDORA_URL_PATH + "/" + getManifestPagePath() + "/files/ExCat0084.jpg/fcr:metadata", itemFilesEntryRdf);
        restGetRdfSuccess(IMAGE_SERVICE_URL_PATH + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0MDA4NC9maWxlcy9FeENhdDAwODQuanBn/info.json", image);
    }

}
