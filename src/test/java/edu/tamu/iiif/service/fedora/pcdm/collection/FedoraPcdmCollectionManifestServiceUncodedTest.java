package edu.tamu.iiif.service.fedora.pcdm.collection;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmCollectionManifestService;
import java.io.IOException;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.InjectMocks;

public class FedoraPcdmCollectionManifestServiceUncodedTest extends AbstractCollectionValid {

    @InjectMocks
    private FedoraPcdmCollectionManifestService fedoraPcdmCollectionManifestService;

    @BeforeEach
    public void setup() {
        super.setup(fedoraPcdmCollectionManifestService);
    }

    protected String getManifestPagePath() {
        return "mwbObjects/TGWCatalog/Pages/ExCat0084";
    }

    protected FedoraPcdmCollectionManifestService getManifestService() {
        return fedoraPcdmCollectionManifestService;
    }

    protected String getMockFilePath(String dir, String file) {
        return super.getMockDirectoryPath() + "uncoded/" + dir + file;
    }

    protected void setupMocks() throws IOException {
        when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestCollectionPath()), eq(String.class))).thenReturn(loadResource(collectionRdf));
        when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath()), eq(String.class))).thenReturn(loadResource(itemRdf));
        when(restTemplate.getForObject(eq(FEDORA_URL + "/" + getManifestPagePath() + "/fcr:metadata"), eq(String.class))).thenReturn(loadResource(itemRdf));
    }

}
