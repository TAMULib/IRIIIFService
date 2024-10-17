package edu.tamu.iiif.service.dspace.rdf.image;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfImageManifestService;
import java.io.IOException;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class DspaceRdfImageManifestServiceDecodedTest extends AbstractImageInvalidSpace {

    @InjectMocks
    private DSpaceRdfImageManifestService dspaceRdfImageManifestService;

    @BeforeEach
    public void setup() {
        super.setup(dspaceRdfImageManifestService);
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

    protected DSpaceRdfImageManifestService getManifestService() {
        return dspaceRdfImageManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "decoded/" + dir + file;
    }

    @Override
    protected void setupMocks() throws IOException {
        when(restTemplate.getForObject(
            eq(IMAGE_SERVICE_URL + "/ZHNwYWNlLXJkZjp4bWx1aS9iaXRzdHJlYW0vMTIzNDU2Nzg5LzEgNTgzMDgvMS9zcG9ydHMtY2FyLTE0Njg3M185NjBfNzIwLnBuZw=="),
            eq(String.class)
        )).thenThrow(new RiotException(SIMULATE_FAILURE));
    }

}