package edu.tamu.iiif.service.fedora;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;

public class FedoraImageManifestServiceTest extends AbstractFedoraManifestServiceTest {

    @InjectMocks
    private FedoraImageManifestService fedoraImageManifestService;

    @Value("classpath:mock/fedora/json/image0.json")
    private Resource image0;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(fedoraImageManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraImageManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraImageManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(fedoraImageManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraImageManifestService, "pcdmRdfExtUrl", PCDM_RDF_URL);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(any(String.class))).thenReturn(FileUtils.readFileToString(image0.getFile(), "UTF-8"));
        String manifest = fedoraImageManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy/pages/page_0/files/PTAR_800x400.png", false));
        Assert.assertEquals(objectMapper.readValue(image0.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
