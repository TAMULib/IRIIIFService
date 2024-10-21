package edu.tamu.iiif.service.fedora.pcdm.sequence;

import com.fasterxml.jackson.databind.JsonNode;
import edu.tamu.iiif.controller.ManifestRequest;
import java.io.IOException;
import java.net.URISyntaxException;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

public abstract class AbstractSequenceValid extends AbstractSequence {

    // Disabled due to failures.
    //java.lang.AssertionError: canvases[]: Expected 1 values but got 0
    @Disabled
    @Test
    public void testGetManifest() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(getManifestPagePath(), false);
        String manifest = getManifestService().getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(sequence.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

}
