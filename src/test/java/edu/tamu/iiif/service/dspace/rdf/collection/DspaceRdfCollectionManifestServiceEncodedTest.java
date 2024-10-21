package edu.tamu.iiif.service.dspace.rdf.collection;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCollectionManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class DspaceRdfCollectionManifestServiceEncodedTest extends AbstractCollectionValid {

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
        return "123456789/1%2058308";
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
        return super.getMockDirectoryPath() + "encoded/" + dir + file;
    }

    @Override
    protected void setupMocks() throws IOException {
        lenient().when(restTemplate.getForObject(eq(DSPACE_URL + "/" + getManifestHandlePath()), eq(String.class))).thenReturn(loadResource(itemRdf));

        lenient().when(restTemplate.getForObject(eq(DSPACE_URL + "/" + PATH_HANDLE + "/" + getManifestCommunity1Path()), eq(String.class))).thenReturn(loadResource(communityRdf));
        lenient().when(restTemplate.getForObject(eq(DSPACE_URL + "/" + PATH_HANDLE + "/" + getManifestCommunity2Path()), eq(String.class))).thenReturn(loadResource(communityRdf));
        when(restTemplate.getForObject(eq(DSPACE_URL + "/" + PATH_HANDLE + "/" + getManifestCollectionPath()), eq(String.class))).thenReturn(loadResource(collectionRdf));
        lenient().when(restTemplate.getForObject(eq(DSPACE_URL + "/" + PATH_HANDLE + "/" + getManifestSubcommunityPath()), eq(String.class))).thenReturn(loadResource(subcommunityRdf));
    }

}
