package edu.tamu.iiif.service.dspace.rdf;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import edu.tamu.iiif.controller.ManifestRequest;

public class DSpaceRdfSequenceManifestServiceTest extends AbstractDSpaceRdfManifestServiceTest {

    @InjectMocks
    private DSpaceRdfSequenceManifestService dspaceRdfSequenceManifestService;

    @Value("classpath:mock/dspace/rdf/item.rdf")
    private Resource rdf;

    @Value("classpath:mock/dspace/json/image.json")
    private Resource image;

    @Value("classpath:mock/dspace/json/sequence.json")
    private Resource sequence;

    @Before
    public void setup() {
        setup(dspaceRdfSequenceManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(restTemplate.getForObject(eq(DSPACE_URL + "/rdf/handle/123456789/158308"), eq(String.class))).thenReturn(readFileToString(rdf.getFile(), "UTF-8"));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png; charset=utf-8");
        when(restTemplate.headForHeaders(eq(DSPACE_URL + "/xmlui/bitstream/123456789/158308/1/sports-car-146873_960_720.png"))).thenReturn(headers);
        when(restTemplate.getForObject(eq(DSPACE_URL + "/rdf/handle/123456789/158308/1/sports-car-146873_960_720.png"), eq(String.class))).thenReturn(readFileToString(rdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(IMAGE_SERVICE_URL + "/ZHNwYWNlLXJkZjp4bWx1aS9iaXRzdHJlYW0vMTIzNDU2Nzg5LzE1ODMwOC8xL3Nwb3J0cy1jYXItMTQ2ODczXzk2MF83MjAucG5n/info.json"), eq(String.class))).thenReturn(readFileToString(image.getFile(), "UTF-8"));
        String manifest = dspaceRdfSequenceManifestService.getManifest(ManifestRequest.of("123456789/158308", false));

        assertEquals(objectMapper.readValue(sequence.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
