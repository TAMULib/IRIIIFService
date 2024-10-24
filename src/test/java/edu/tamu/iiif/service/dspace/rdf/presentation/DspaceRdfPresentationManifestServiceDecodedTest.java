package edu.tamu.iiif.service.dspace.rdf.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;

import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfPresentationManifestService;
import java.io.IOException;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class DspaceRdfPresentationManifestServiceDecodedTest extends AbstractPresentationInvalidSpace {

    @InjectMocks
    private DSpaceRdfPresentationManifestService dspaceRdfPresentationManifestService;

    @BeforeEach
    public void setup() {
        super.setup(dspaceRdfPresentationManifestService);
    }

    protected String getManifestCollectionPath() {
        return "123456789/1 58299";
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

    protected DSpaceRdfPresentationManifestService getManifestService() {
        return dspaceRdfPresentationManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "decoded/" + dir + file;
    }

    @Override
    protected void setupMocks() throws IOException {
        lenient().when(restTemplate.getForObject(eq(DSPACE_URL + "/" + getManifestHandlePath()), eq(String.class))).thenThrow(new RiotException(SIMULATE_FAILURE));
        lenient().when(restTemplate.getForObject(eq(DSPACE_URL + "/" + PATH_HANDLE + "/" + getManifestCollectionPath()), eq(String.class))).thenThrow(new RiotException(SIMULATE_FAILURE));
    }

}
