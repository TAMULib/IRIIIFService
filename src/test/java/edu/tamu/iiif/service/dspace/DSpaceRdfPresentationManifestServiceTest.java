package edu.tamu.iiif.service.dspace;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.RedisManifest;

public class DSpaceRdfPresentationManifestServiceTest extends AbstractDSpaceRdfManifestServiceTest {

    @InjectMocks
    private DSpaceRdfPresentationManifestService dspaceRdfPresentationManifestService;

    @Value("classpath:mock/dspace/rdf/item.rdf")
    private Resource rdf;

    @Value("classpath:mock/dspace/json/image.json")
    private Resource image;

    @Value("classpath:mock/dspace/json/presentation.json")
    private Resource presentation;

    @Before
    public void setup() {
        setup(dspaceRdfPresentationManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        setupMocks();
        String manifest = dspaceRdfPresentationManifestService.getManifest(ManifestRequest.of("123456789/158308", false));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestAllowed() throws IOException, URISyntaxException {
        RedisManifest redisManifest = new RedisManifest("123456789/158308", PRESENTATION, DSPACE_RDF_IDENTIFIER, readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = dspaceRdfPresentationManifestService.getManifest(ManifestRequest.of("123456789/158308", false, Arrays.asList(new String[] { "image/png", "image/jpeg" }), Arrays.asList(new String[] {})));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestDisallowed() throws IOException, URISyntaxException {
        RedisManifest redisManifest = new RedisManifest("123456789/158308", PRESENTATION, DSPACE_RDF_IDENTIFIER, readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = dspaceRdfPresentationManifestService.getManifest(ManifestRequest.of("123456789/158308", false, Arrays.asList(new String[] {}), Arrays.asList(new String[] { "image/bmp", "image/jpeg" })));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestCached() throws IOException, URISyntaxException {
        RedisManifest redisManifest = new RedisManifest("123456789/158308", PRESENTATION, DSPACE_RDF_IDENTIFIER, readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = dspaceRdfPresentationManifestService.getManifest(ManifestRequest.of("123456789/158308", false));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestUpdateCached() throws IOException, URISyntaxException {
        setupMocks();
        RedisManifest redisManifest = new RedisManifest("123456789/158308", PRESENTATION, DSPACE_RDF_IDENTIFIER, readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = dspaceRdfPresentationManifestService.getManifest(ManifestRequest.of("123456789/158308", true));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    private void setupMocks() throws IOException {
        when(httpService.get(eq(DSPACE_URL + "/rdf/handle/123456789/158308"))).thenReturn(readFileToString(rdf.getFile(), "UTF-8"));
        when(httpService.contentType(eq(DSPACE_URL + "/xmlui/bitstream/123456789/158308/1/sports-car-146873_960_720.png"))).thenReturn("image/png; charset=utf-8");
        when(httpService.get(eq(DSPACE_URL + "/rdf/handle/123456789/158308/1/sports-car-146873_960_720.png"))).thenReturn(readFileToString(rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZHNwYWNlLXJkZjp4bWx1aS9iaXRzdHJlYW0vMTIzNDU2Nzg5LzE1ODMwOC8xL3Nwb3J0cy1jYXItMTQ2ODczXzk2MF83MjAucG5n/info.json"))).thenReturn(FileUtils.readFileToString(image.getFile(), "UTF-8"));
    }

}
