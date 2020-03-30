package edu.tamu.iiif.service.dspace.rdf;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import edu.tamu.iiif.controller.ManifestRequest;

public class DSpaceRdfImageManifestServiceTest extends AbstractDSpaceRdfManifestServiceTest {

    @InjectMocks
    private DSpaceRdfImageManifestService dspaceRdfImageManifestService;

    @Value("classpath:mock/dspace/json/image.json")
    private Resource image;

    @Before
    public void setup() {
        setup(dspaceRdfImageManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(readFileToString(image.getFile(), "UTF-8"));
        String manifest = dspaceRdfImageManifestService.getManifest(ManifestRequest.of("123456789/158308/1/sports-car-146873_960_720.png", false));
        assertEquals(objectMapper.readValue(image.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
