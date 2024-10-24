package edu.tamu.iiif.service.fedora.pcdm.sequence;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmSequenceManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;
import org.springframework.http.HttpHeaders;

public class FedoraPcdmSequenceManifestServiceEncodedTest extends AbstractSequenceValid {

    @InjectMocks
    private FedoraPcdmSequenceManifestService fedoraPcdmSequenceManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmSequenceManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat%200084";
    }

    protected FedoraPcdmSequenceManifestService getManifestService() {
        return fedoraPcdmSequenceManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "encoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png; charset=utf-8");
        when(restTemplate.headForHeaders(any(String.class))).thenReturn(headers);

        when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath()), eq(String.class))).thenReturn(loadResource(itemRdf));
        when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath() + "/files/fcr:metadata"), eq(String.class))).thenReturn(loadResource(itemFilesRdf));
        when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath() + "/files/ExCat0084.jpg/fcr:metadata"), eq(String.class))).thenReturn(loadResource(itemFilesEntryRdf));
        when(restTemplate.getForObject(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0JTIwMDA4NC9maWxlcy9FeENhdDAwODQuanBn/info.json"), eq(String.class))).thenReturn(loadResource(image));
    }

}
