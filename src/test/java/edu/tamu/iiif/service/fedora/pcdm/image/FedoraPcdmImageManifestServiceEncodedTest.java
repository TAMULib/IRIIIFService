package edu.tamu.iiif.service.fedora.pcdm.image;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmImageManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class FedoraPcdmImageManifestServiceEncodedTest extends AbstractImageValid {

    @InjectMocks
    private FedoraPcdmImageManifestService fedoraPcdmImageManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmImageManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat%200084";
    }

    protected FedoraPcdmImageManifestService getManifestService() {
        return fedoraPcdmImageManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "encoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        when(restTemplate.getForObject(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0JTIwMDA4NA=="), eq(String.class))).thenReturn(loadResource(image));
    }

}