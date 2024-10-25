package edu.tamu.iiif.service.dspace.rdf.sequence;

import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfSequenceManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class DspaceRdfSequenceManifestServiceDecodedTest extends AbstractSequenceInvalidSpace {

    @InjectMocks
    private DSpaceRdfSequenceManifestService dspaceRdfSequenceManifestService;

    @BeforeEach
    public void setup() {
        super.setup(dspaceRdfSequenceManifestService);
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

    protected DSpaceRdfSequenceManifestService getManifestService() {
        return dspaceRdfSequenceManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "decoded/" + dir + file;
    }

    @Override
    protected void setupMocks() throws IOException {
        restGetRdfBadRequest(DSPACE_URL_PATH + "/" + getManifestHandlePath());
    }

}
