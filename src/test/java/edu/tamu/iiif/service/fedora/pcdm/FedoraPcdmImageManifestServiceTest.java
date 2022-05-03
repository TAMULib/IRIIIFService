package edu.tamu.iiif.service.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;

@ExtendWith(MockitoExtension.class)
public class FedoraPcdmImageManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmImageManifestService fedoraPcdmImageManifestService;

    @Value("classpath:mock/fedora/json/image.json")
    private Resource image;

    @BeforeEach
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
