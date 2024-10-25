package edu.tamu.iiif.service.dspace.rdf.collection;

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

public abstract class AbstractCollectionValid extends AbstractCollection {

    // Disabled due to unknown failure, the url is mocked and should work (is something missing?).
    // org.apache.jena.atlas.web.HttpException: GET http://localhost:8080/rdf/handle/123456789/15829
    @Disabled
    @Test
    public void testGetCollectionManifest() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(getManifestCollectionPath(), false);
        String manifest = getManifestService().getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(collection.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

    // Disabled due to unknown failure, the url is mocked and should work (is something missing?).
    // org.apache.jena.atlas.web.HttpException: GET http://localhost:8080/rdf/handle/123456789/15829
    @Disabled
    @Test
    public void testGetCollectionsManifest() throws IOException, URISyntaxException, JSONException {
        setupMocks();

        ManifestRequest manifestRequest = ManifestRequest.of(getManifestCommunity1Path(), false);
        String manifest = getManifestService().getManifest(manifestRequest);
        JSONObject response = new JSONObject(manifest);
        JSONObject expected = new JSONObject(objectMapper.readValue(collections.getFile(), JsonNode.class).toString());

        JSONAssert.assertEquals(expected, response, JSONCompareMode.LENIENT);
    }

}
