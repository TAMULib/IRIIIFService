package edu.tamu.iiif.service.dspace.rdf.presentation;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;
import static org.mockito.ArgumentMatchers.any;
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
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public abstract class AbstractPresentationValid extends AbstractPresentation {

    @Test
    public void testGetManifest() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(getManifestItemPath(), false);
        String manifest = getManifestService().getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentation.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetCollectionManifest() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(getManifestCollectionPath(), false);
        String manifest = getManifestService().getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(collectionPresentation.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestAllowed() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(getManifestItemPath(), false, Arrays.asList(new String[] { "image/png", "image/jpeg" }), Arrays.asList(new String[] {}));
        String manifest = getManifestService().getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentationAllow.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestDisallowed() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(getManifestItemPath(), false, Arrays.asList(new String[] {}), Arrays.asList(new String[] { "image/bmp", "image/jpeg" }));
        String manifest = getManifestService().getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentationDisallow.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestCached() throws IOException, URISyntaxException, JSONException {
        RedisManifest redisManifest = new RedisManifest(getManifestItemPath(), PRESENTATION, DSPACE_RDF_IDENTIFIER, loadResource(presentation));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class)))
            .thenReturn(Optional.of(redisManifest));

        String manifest = getManifestService().getManifest(ManifestRequest.of(getManifestItemPath(), false));
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentation.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestUpdateCached() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        RedisManifest redisManifest = new RedisManifest(getManifestItemPath(), PRESENTATION, DSPACE_RDF_IDENTIFIER, loadResource(presentation));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class)))
            .thenReturn(Optional.of(redisManifest));

        ManifestRequest manifestRequest = ManifestRequest.of(getManifestItemPath(), true);
        String manifest = getManifestService().getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentation.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

}
