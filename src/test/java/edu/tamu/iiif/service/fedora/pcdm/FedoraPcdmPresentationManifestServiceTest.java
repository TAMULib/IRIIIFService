package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.JsonNode;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.RedisManifest;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

@ExtendWith(MockitoExtension.class)
public class FedoraPcdmPresentationManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    private static final String MANIFEST_PATH = "mwbObjects/TGWCatalog/Pages/ExCat%200084";

    @InjectMocks
    private FedoraPcdmPresentationManifestService fedoraPcdmPresentationManifestService;

    @Value("classpath:mock/fedora/rdf/item_container.rdf")
    private Resource itemRdf;

    @Value("classpath:mock/fedora/rdf/item_container-encoded.rdf")
    private Resource itemRdfEncoded;

    @Value("classpath:mock/fedora/rdf/item_container_files.rdf")
    private Resource itemFilesRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files-encoded.rdf")
    private Resource itemFilesRdfEncoded;

    @Value("classpath:mock/fedora/rdf/item_container_files_entry.rdf")
    private Resource itemFilesEntryRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files_entry-encoded.rdf")
    private Resource itemFilesEntryRdfEncoded;

    @Value("classpath:mock/fedora/json/image.json")
    private Resource image;

    @Value("classpath:mock/fedora/json/image-encoded.json")
    private Resource imageEncoded;

    @Value("classpath:mock/fedora/json/presentation.json")
    private Resource presentation;

    @Value("classpath:mock/fedora/json/presentation-encoded.json")
    private Resource presentationEncoded;

    @Value("classpath:mock/fedora/json/presentation-allow.json")
    private Resource presentationAllow;

    @Value("classpath:mock/fedora/json/presentation-allow-encoded.json")
    private Resource presentationAllowEncoded;

    @Value("classpath:mock/fedora/json/presentation-disallow.json")
    private Resource presentationDisallow;

    @Value("classpath:mock/fedora/json/presentation-disallow-encoded.json")
    private Resource presentationDisallowEncoded;

    private final static boolean DEBUG_DISABLE_DECODE = "true".equalsIgnoreCase(System.getenv("DEBUG_DISABLE_URL_DECODE"));

    @BeforeEach
    public void setup() {
        setup(fedoraPcdmPresentationManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(MANIFEST_PATH, false);
        String manifest = fedoraPcdmPresentationManifestService.getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentation.getFile(), JsonNode.class).toPrettyString());

        if (DEBUG_DISABLE_DECODE) {
            expected = new JSONObject(objectMapper.readValue(presentationEncoded.getFile(), JsonNode.class).toPrettyString());
        }

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestAllowed() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(MANIFEST_PATH, false, Arrays.asList(new String[] { "image/png", "image/jpeg" }), Arrays.asList(new String[] {}));
        String manifest = fedoraPcdmPresentationManifestService.getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentationAllow.getFile(), JsonNode.class).toPrettyString());

        if (DEBUG_DISABLE_DECODE) {
            expected = new JSONObject(objectMapper.readValue(presentationAllowEncoded.getFile(), JsonNode.class).toPrettyString());
        }

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestDisallowed() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(MANIFEST_PATH, false, Arrays.asList(new String[] {}), Arrays.asList(new String[] { "image/bmp", "image/jpeg" }));
        String manifest = fedoraPcdmPresentationManifestService.getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentationDisallow.getFile(), JsonNode.class).toPrettyString());

        if (DEBUG_DISABLE_DECODE) {
            expected = new JSONObject(objectMapper.readValue(presentationDisallowEncoded.getFile(), JsonNode.class).toPrettyString());
        }

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestCached() throws IOException, URISyntaxException, JSONException {
        RedisManifest redisManifest = new RedisManifest(MANIFEST_PATH, PRESENTATION, FEDORA_PCDM_IDENTIFIER, readFileToString((DEBUG_DISABLE_DECODE) ? presentationEncoded.getFile() : presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));

        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of(MANIFEST_PATH, false));
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentation.getFile(), JsonNode.class).toPrettyString());

        if (DEBUG_DISABLE_DECODE) {
            expected = new JSONObject(objectMapper.readValue(presentationEncoded.getFile(), JsonNode.class).toPrettyString());
        }

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestUpdateCached() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        RedisManifest redisManifest = new RedisManifest(MANIFEST_PATH, PRESENTATION, FEDORA_PCDM_IDENTIFIER, readFileToString((DEBUG_DISABLE_DECODE) ? presentationEncoded.getFile() : presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));

        ManifestRequest manifestRequest = ManifestRequest.of(MANIFEST_PATH, true);
        String manifest = fedoraPcdmPresentationManifestService.getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentation.getFile(), JsonNode.class).toPrettyString());

        if (DEBUG_DISABLE_DECODE) {
            expected = new JSONObject(objectMapper.readValue(presentationEncoded.getFile(), JsonNode.class).toPrettyString());
        }

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    private void setupMocks() throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png; charset=utf-8");
        when(restTemplate.headForHeaders(any(String.class))).thenReturn(headers);

        lenient().when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat%200084"), eq(String.class))).thenReturn(readFileToString(itemRdfEncoded.getFile(), "UTF-8"));
        lenient().when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat%200084/files/fcr:metadata"), eq(String.class))).thenReturn(readFileToString(itemFilesRdfEncoded.getFile(), "UTF-8"));
        lenient().when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat%200084/files/ExCat0084.jpg/fcr:metadata"), eq(String.class))).thenReturn(readFileToString(itemFilesEntryRdfEncoded.getFile(), "UTF-8"));
        lenient().when(restTemplate.getForObject(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0JTIwMDA4NC9maWxlcy9FeENhdDAwODQuanBn/info.json"), eq(String.class))).thenReturn(readFileToString(imageEncoded.getFile(), "UTF-8"));

        lenient().when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat 0084"), eq(String.class))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));
        lenient().when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat 0084/files/fcr:metadata"), eq(String.class))).thenReturn(readFileToString(itemFilesRdf.getFile(), "UTF-8"));
        lenient().when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat 0084/files/ExCat0084.jpg/fcr:metadata"), eq(String.class))).thenReturn(readFileToString(itemFilesEntryRdf.getFile(), "UTF-8"));
        lenient().when(restTemplate.getForObject(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06bXdiT2JqZWN0cy9UR1dDYXRhbG9nL1BhZ2VzL0V4Q2F0IDAwODQvZmlsZXMvRXhDYXQwMDg0LmpwZw==/info.json"), eq(String.class))).thenReturn(readFileToString(image.getFile(), "UTF-8"));
    }

}
