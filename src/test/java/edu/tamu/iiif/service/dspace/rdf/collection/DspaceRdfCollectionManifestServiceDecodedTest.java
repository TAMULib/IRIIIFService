package edu.tamu.iiif.service.dspace.rdf.collection;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCollectionManifestService;
import java.io.IOException;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class DspaceRdfCollectionManifestServiceDecodedTest extends AbstractCollectionInvalidSpace {

    @InjectMocks
    private DSpaceRdfCollectionManifestService dspaceRdfCollectionManifestService;

    @BeforeEach
    public void setup() {
        super.setup(dspaceRdfCollectionManifestService);
    }

    protected String getManifestCollectionPath() {
        return "123456789/158299";
    }

    protected String getManifestCommunity1Path() {
        return "123456789/158298";
    }

    protected String getManifestCommunity2Path() {
        return "123456789/158302";
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

    protected DSpaceRdfCollectionManifestService getManifestService() {
        return dspaceRdfCollectionManifestService;
    }

    protected String getManifestSubcommunityPath() {
        return "123456789/158301";
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "decoded/" + dir + file;
    }

    @Override
    protected void setupMocks() throws IOException {
        when(restTemplate.getForObject(eq(DSPACE_URL + "/" + PATH_HANDLE + "/" + getManifestCollectionPath()), eq(String.class))).thenThrow(new RiotException(SIMULATE_FAILURE));
    }

}
