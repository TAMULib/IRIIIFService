package edu.tamu.iiif.service.fedora.pcdm;

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

public class FedoraPcdmPresentationManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmPresentationManifestService fedoraPcdmPresentationManifestService;

    @Value("classpath:mock/fedora/rdf/item_container.rdf")
    private Resource itemRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files.rdf")
    private Resource itemFilesRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files_entry.rdf")
    private Resource itemFilesEntryRdf;

    @Value("classpath:mock/fedora/json/image.json")
    private Resource image;

    @Value("classpath:mock/fedora/json/presentation.json")
    private Resource presentation;

    @Value("classpath:mock/fedora/json/presentation-allow.json")
    private Resource presentationAllow;

    @Value("classpath:mock/fedora/json/presentation-disallow.json")
    private Resource presentationDisallow;

    @Before
    public void setup() {
        setup(fedoraPcdmPresentationManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        setupMocks();
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog/Pages/ExCat0084", false));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestAllowed() throws IOException, URISyntaxException {
        setupMocks();
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog/Pages/ExCat0084", false, Arrays.asList(new String[] { "image/png", "image/jpeg" }), Arrays.asList(new String[] {})));
        assertEquals(objectMapper.readValue(presentationAllow.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestDisallowed() throws IOException, URISyntaxException {
        setupMocks();
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog/Pages/ExCat0084", false, Arrays.asList(new String[] {}), Arrays.asList(new String[] { "image/bmp", "image/jpeg" })));
        assertEquals(objectMapper.readValue(presentationDisallow.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestCached() throws IOException, URISyntaxException {
        RedisManifest redisManifest = new RedisManifest("mwbObjects/TGWCatalog/Pages/ExCat0084", PRESENTATION, FEDORA_PCDM_IDENTIFIER, readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog/Pages/ExCat0084", false));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestUpdateCached() throws IOException, URISyntaxException {
        setupMocks();
        RedisManifest redisManifest = new RedisManifest("mwbObjects/TGWCatalog/Pages/ExCat0084", PRESENTATION, FEDORA_PCDM_IDENTIFIER, readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog/Pages/ExCat0084", true));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestWithoutOrder() throws IOException, URISyntaxException {

    }

    private void setupMocks() throws IOException {
        when(httpService.contentType(any(String.class))).thenReturn("image/png; charset=utf-8");
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084"))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/files/fcr:metadata"))).thenReturn(readFileToString(itemFilesRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/files/ExCat0084.jpg/fcr:metadata"))).thenReturn(readFileToString(itemFilesEntryRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0MDA4NC9maWxlcy9FeENhdDAwODQuanBn/info.json"))).thenReturn(FileUtils.readFileToString(image.getFile(), "UTF-8"));
    }

}
