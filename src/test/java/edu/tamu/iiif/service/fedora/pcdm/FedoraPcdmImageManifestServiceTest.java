package edu.tamu.iiif.service.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
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

public class FedoraPcdmImageManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmImageManifestService fedoraPcdmImageManifestService;

    @Value("classpath:mock/fedora/json/image.json")
    private Resource image;

    @Before
    public void setup() {
        setup(fedoraPcdmImageManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(readFileToString(image.getFile(), "UTF-8"));
        String manifest = fedoraPcdmImageManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog/Pages/ExCat0084/files/ExCat0084.jpg", false));
        assertEquals(objectMapper.readValue(image.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
