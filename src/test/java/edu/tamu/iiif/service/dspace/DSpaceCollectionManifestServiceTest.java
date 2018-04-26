package edu.tamu.iiif.service.dspace;

import static org.mockito.Matchers.eq;
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

public class DSpaceCollectionManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpaceCollectionManifestService dSpaceCollectionManifestService;

    @Value("classpath:mock/dspace/rdf/collection.rdf")
    private Resource rdf;

    @Value("classpath:mock/dspace/json/collection.json")
    private Resource collection;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "dspaceUrl", DSPACE_URL);
        ReflectionTestUtils.setField(dSpaceCollectionManifestService, "dspaceWebapp", DSPACE_WEBAPP);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(eq(DSPACE_URL + "/rdf/handle/123456789/158299"))).thenReturn(FileUtils.readFileToString(rdf.getFile(), "UTF-8"));
        String manifest = dSpaceCollectionManifestService.getManifest("123456789/158299", false);

        Assert.assertEquals(objectMapper.readValue(collection.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
