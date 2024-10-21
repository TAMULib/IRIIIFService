package edu.tamu.iiif.service.dspace.rdf.canvas;

import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCanvasManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class DspaceRdfCanvasManifestServiceEncodedTest extends AbstractCanvasValid {

    @InjectMocks
    private DSpaceRdfCanvasManifestService dspaceRdfCanvasManifestService;

    @BeforeEach
    public void setup() {
        super.setup(dspaceRdfCanvasManifestService);
    }

    protected String getManifestFilePath() {
        return "1/sports-car-146873_960_720.png";
    }

    protected String getManifestHandlePath() {
        return PATH_HANDLE + "/" + getManifestItemPath();
    }

    protected String getManifestItemPath() {
        return "123456789/1%2058308";
    }

    protected String getManifestRequestPath() {
        return getManifestItemPath() + "/" + getManifestFilePath();
    }

    protected DSpaceRdfCanvasManifestService getManifestService() {
        return dspaceRdfCanvasManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "encoded/" + dir + file;
    }

    @Override
    protected void setupMocks() throws IOException {
        restGetImageSuccess(DSPACE_URL_PATH + "/" + getManifestBitstreamPath(), "image/png");
        restGetRdfSuccess(DSPACE_URL_PATH + "/" + getManifestHandlePath(), itemRdf);
        restGetRdfSuccess(IMAGE_SERVICE_URL_PATH + "/ZHNwYWNlLXJkZjp4bWx1aS9iaXRzdHJlYW0vMTIzNDU2Nzg5LzElMjA1ODMwOC8xL3Nwb3J0cy1jYXItMTQ2ODczXzk2MF83MjAucG5n/info.json", image);
    }

}
