package edu.tamu.iiif.service.dspace.rdf.canvas;

import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCanvasManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class DspaceRdfCanvasManifestServiceDecodedTest extends AbstractCanvasInvalidSpace {

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
        return "123456789/1 58308";
    }

    protected String getManifestRequestPath() {
        return getManifestItemPath() + "/" + getManifestFilePath();
    }

    protected DSpaceRdfCanvasManifestService getManifestService() {
        return dspaceRdfCanvasManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "decoded/" + dir + file;
    }

    @Override
    protected void setupMocks() throws IOException {
        restGetRdfBadRequest(DSPACE_URL_PATH + "/" + getManifestHandlePath());
    }

}
