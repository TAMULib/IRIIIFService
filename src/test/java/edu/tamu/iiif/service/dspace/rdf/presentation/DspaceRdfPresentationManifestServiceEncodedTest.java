package edu.tamu.iiif.service.dspace.rdf.presentation;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfPresentationManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.springframework.http.HttpHeaders;

public class DspaceRdfPresentationManifestServiceEncodedTest extends AbstractPresentationValid {

    @InjectMocks
    private DSpaceRdfPresentationManifestService dspaceRdfPresentationManifestService;

    @BeforeEach
    public void setup() {
        super.setup(dspaceRdfPresentationManifestService);
    }

    protected String getManifestCollectionPath() {
        return "123456789/158299";
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

    protected DSpaceRdfPresentationManifestService getManifestService() {
        return dspaceRdfPresentationManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "encoded/" + dir + file;
    }

    @Override
    protected void setupMocks() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png; charset=utf-8");
        when(restTemplate.headForHeaders(eq(DSPACE_URL + "/" + getManifestBitstreamPath()))).thenReturn(headers);

        when(restTemplate.getForObject(eq(DSPACE_URL + "/" + getManifestHandlePath()), eq(String.class))).thenReturn(loadResource(itemRdf));
        lenient().when(restTemplate.getForObject(eq(DSPACE_URL + "/" + PATH_HANDLE + "/" + getManifestCollectionPath()), eq(String.class))).thenReturn(loadResource(collectionRdf));
        when(restTemplate.getForObject(eq(IMAGE_SERVICE_URL + "/ZHNwYWNlLXJkZjp4bWx1aS9iaXRzdHJlYW0vMTIzNDU2Nzg5LzElMjA1ODMwOC8xL3Nwb3J0cy1jYXItMTQ2ODczXzk2MF83MjAucG5n/info.json"), eq(String.class))).thenReturn(loadResource(image));
    }

}
