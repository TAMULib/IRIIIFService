package edu.tamu.iiif.service.dspace.rdf;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
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
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCanvasManifestService;

public class DSpaceRdfCanvasManifestServiceTest extends AbstractDSpaceRdfManifestServiceTest {

    @InjectMocks
    private DSpaceRdfCanvasManifestService dspaceRdfCanvasManifestService;

    @Value("classpath:mock/dspace/rdf/item.rdf")
    private Resource rdf;

    @Value("classpath:mock/dspace/json/image.json")
    private Resource image;

    @Value("classpath:mock/dspace/json/canvas.json")
    private Resource canvas;

    @Before
    public void setup() {
        setup(dspaceRdfCanvasManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(eq(DSPACE_URL + "/rdf/handle/123456789/158308"))).thenReturn(readFileToString(rdf.getFile(), "UTF-8"));
        when(httpService.contentType(eq(DSPACE_URL + "/xmlui/bitstream/123456789/158308/1/sports-car-146873_960_720.png"))).thenReturn("image/png; charset=utf-8");
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZHNwYWNlLXJkZjp4bWx1aS9iaXRzdHJlYW0vMTIzNDU2Nzg5LzE1ODMwOC8xL3Nwb3J0cy1jYXItMTQ2ODczXzk2MF83MjAucG5n/info.json"))).thenReturn(readFileToString(image.getFile(), "UTF-8"));
        String manifest = dspaceRdfCanvasManifestService.getManifest(ManifestRequest.of("123456789/158308/1/sports-car-146873_960_720.png", false));
        assertEquals(objectMapper.readValue(canvas.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
