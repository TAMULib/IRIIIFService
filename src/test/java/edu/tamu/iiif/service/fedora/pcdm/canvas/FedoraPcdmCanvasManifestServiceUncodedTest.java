package edu.tamu.iiif.service.fedora.pcdm.canvas;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmCanvasManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class FedoraPcdmCanvasManifestServiceUncodedTest extends AbstractCanvasValid {

    @InjectMocks
    private FedoraPcdmCanvasManifestService fedoraPcdmCanvasManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmCanvasManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat0084";
    }
    protected String getManifestHandlePath() {
        return "rdf/handle/123456789/158308";
    }

    protected FedoraPcdmCanvasManifestService getManifestService() {
        return fedoraPcdmCanvasManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "uncoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        restGetRdfSuccess(FEDORA_URL_PATH + "/" + getManifestPagePath(), itemRdf);
        restGetRdfSuccess(FEDORA_URL_PATH + "/" + getManifestPagePath() + "/files/fcr:metadata", itemFilesRdf);
        restGetRdfSuccess(FEDORA_URL_PATH + "/" + getManifestPagePath() + "/files/ExCat0084.jpg/fcr:metadata", itemFilesEntryRdf);
        restGetRdfSuccess(IMAGE_SERVICE_URL_PATH + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0MDA4NC9maWxlcy9FeENhdDAwODQuanBn/info.json", image);
    }

}
