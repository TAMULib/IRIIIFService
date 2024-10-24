package edu.tamu.iiif.service.fedora.pcdm.presentation;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmPresentationManifestService;
import java.io.IOException;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.springframework.http.HttpHeaders;

public class FedoraPcdmPresentationManifestServiceDecodedTest extends AbstractPresentationInvalidSpace {

    @InjectMocks
    private FedoraPcdmPresentationManifestService fedoraPcdmPresentationManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmPresentationManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat 0084";
    }

    protected FedoraPcdmPresentationManifestService getManifestService() {
        return fedoraPcdmPresentationManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "encoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png; charset=utf-8");
        lenient().when(restTemplate.headForHeaders(any(String.class))).thenReturn(headers);

        when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath()), eq(String.class))).thenThrow(new RiotException(SIMULATE_FAILURE));
        lenient().when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath() + "/files/fcr:metadata"), eq(String.class))).thenReturn(loadResource(itemFilesRdf));
        lenient().when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath() + "/files/ExCat0084.jpg/fcr:metadata"), eq(String.class))).thenReturn(loadResource(itemFilesEntryRdf));
        lenient().when(restTemplate.getForObject(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0JTIwMDA4NC9maWxlcy9FeENhdDAwODQuanBn/info.json"), eq(String.class))).thenReturn(loadResource(image));
    }

}
