package edu.tamu.iiif.service.dspace;

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

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;

public class DSpaceImageManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpaceImageManifestService dSpaceImageManifestService;

    @Value("classpath:mock/dspace/json/image.json")
    private Resource image;

    @Before
    public void setup() {
        setup(dSpaceImageManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(any(String.class))).thenReturn(FileUtils.readFileToString(image.getFile(), "UTF-8"));
        String manifest = dSpaceImageManifestService.getManifest(ManifestRequest.of("123456789/158308/1/sports-car-146873_960_720.png", false));
        Assert.assertEquals(objectMapper.readValue(image.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
