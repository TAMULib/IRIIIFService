package edu.tamu.iiif.service.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmImageManifestService;

public class FedoraPcdmImageManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmImageManifestService fedoraPcdmImageManifestService;

    @Value("classpath:mock/fedora/json/image0.json")
    private Resource image0;

    @Before
    public void setup() {
        setup(fedoraPcdmImageManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(any(String.class))).thenReturn(readFileToString(image0.getFile(), "UTF-8"));
        String manifest = fedoraPcdmImageManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy/pages/page_0/files/PTAR_800x400.png", false));
        assertEquals(objectMapper.readValue(image0.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
