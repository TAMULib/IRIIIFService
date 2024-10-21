package edu.tamu.iiif.service.fedora.pcdm.presentation;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import org.apache.jena.irix.IRIException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public abstract class AbstractPresentationInvalidSpace extends AbstractPresentation {

    @Test
    public void testGetManifest() throws IOException {
        setupMocks();

        performAssertionCheck(ManifestRequest.of(getManifestPagePath(), false));
    }

    @Test
    public void testGetManifestAllowed() throws IOException {
        setupMocks();

        performAssertionCheck(ManifestRequest.of(getManifestPagePath(), false, Arrays.asList(new String[] { "image/png", "image/jpeg" }), Arrays.asList(new String[] {})));
    }

    @Test
    public void testGetManifestDisallowed() throws IOException {
        setupMocks();

        performAssertionCheck(ManifestRequest.of(getManifestPagePath(), false, Arrays.asList(new String[] {}), Arrays.asList(new String[] { "image/bmp", "image/jpeg" })));
    }

    @Test
    public void testGetManifestCached() throws IOException, JSONException, URISyntaxException {
        RedisManifest redisManifest = new RedisManifest(getManifestPagePath(), PRESENTATION, FEDORA_PCDM_IDENTIFIER, loadResource(presentation));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class)))
            .thenReturn(Optional.of(redisManifest));

        // This test does not fail because the manifest is already cached.
        // The cached manifest itself is not validated and so no errors are thrown.
        String manifest = getManifestService().getManifest(ManifestRequest.of(getManifestPagePath(), false));
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(presentation.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    @Test
    public void testGetManifestUpdateCached() throws IOException {
        setupMocks();

        RedisManifest redisManifest = new RedisManifest(getManifestPagePath(), PRESENTATION, FEDORA_PCDM_IDENTIFIER, loadResource(presentation));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class)))
            .thenReturn(Optional.of(redisManifest));

        performAssertionCheck(ManifestRequest.of(getManifestPagePath(), true));
    }

    /**
     * Perform the exception assertion.
     *
     * This is inteded to reduce code repitition in the tests.
     *
     * @param manifestRequest The built manifest request.
     */
    private void performAssertionCheck(ManifestRequest manifestRequest) {
        assertThrows(IRIException.class, () -> {
            getManifestService().getManifest(manifestRequest);
        });
    }

}
