package edu.tamu.iiif.service.fedora.pcdm.canvas;

import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmCanvasManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;

@SpringBootTest
@RestClientTest(FedoraPcdmCanvasManifestService.class)
public class FedoraPcdmCanvasManifestServiceEncodedTest extends AbstractCanvasValid {

    @Autowired
    private FedoraPcdmCanvasManifestService fedoraPcdmCanvasManifestService;

    @Autowired
    private MockRestServiceServer mockServer;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmCanvasManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat%200084";
    }

    protected FedoraPcdmCanvasManifestService getManifestService() {
        return fedoraPcdmCanvasManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "encoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        //HttpHeaders headers = new HttpHeaders();
        //headers.add("Content-Type", "image/png; charset=utf-8");
        //when(restTemplate.headForHeaders(any(String.class))).thenReturn(headers);

        //when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath()), eq(String.class))).thenReturn(loadResource(itemRdf));
        //when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath() + "/files/fcr:metadata"), eq(String.class))).thenReturn(loadResource(itemFilesRdf));
        //when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath() + "/files/ExCat0084.jpg/fcr:metadata"), eq(String.class))).thenReturn(loadResource(itemFilesEntryRdf));
        //when(restTemplate.getForObject(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0JTIwMDA4NC9maWxlcy9FeENhdDAwODQuanBn/info.json"), eq(String.class))).thenReturn(loadResource(image));

        //http://localhost:9000/fcrepo/rest/mwbObjects/TGWCatalog/Pages/ExCat%200084
        System.out.print("\n\n\nDEBUG: should be mocking this request path: " + FEDORA_URL + "/" + getManifestPagePath() + "\n\n\n");
        mockServer.expect(requestTo(FEDORA_URL + "/" + getManifestPagePath()))
            .andRespond(withSuccess(loadResource(itemRdf), MediaType.valueOf(MIMETYPE_TURTLE)));

        mockServer.expect(requestTo(FEDORA_URL + "/" + getManifestPagePath() + "/files/fcr:metadata"))
            .andRespond(withSuccess(loadResource(itemFilesRdf), MediaType.valueOf(MIMETYPE_TURTLE)));

        mockServer.expect(requestTo(FEDORA_URL + "/" + getManifestPagePath() + "/files/ExCat0084.jpg/fcr:metadata"))
            .andRespond(withSuccess(loadResource(itemFilesEntryRdf), MediaType.valueOf(MIMETYPE_TURTLE)));

        mockServer.expect(requestTo(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0JTIwMDA4NC9maWxlcy9FeENhdDAwODQuanBn/info.json"))
            .andRespond(withSuccess(loadResource(image), MediaType.valueOf(MIMETYPE_TURTLE)));
    }

}
