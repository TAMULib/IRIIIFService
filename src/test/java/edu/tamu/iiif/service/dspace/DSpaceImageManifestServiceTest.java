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
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;

public class DSpaceImageManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpaceImageManifestService dSpaceImageManifestService;

    @Value("classpath:mock/dspace/json/image.json")
    private Resource image;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(dSpaceImageManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceImageManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceImageManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(dSpaceImageManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dSpaceImageManifestService, "dspaceWebapp", DSPACE_WEBAPP);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(any(String.class))).thenReturn(FileUtils.readFileToString(image.getFile(), "UTF-8"));
        String manifest = dSpaceImageManifestService.getManifest("123456789/158308/1/sports-car-146873_960_720.png", false);
        Assert.assertEquals(objectMapper.readValue(image.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
