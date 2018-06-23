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

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;

public class DSpaceCollectionManifestServiceTest extends AbstractDSpaceManifestServiceTest {

    @InjectMocks
    private DSpaceCollectionManifestService dSpaceCollectionManifestService;

    @Value("classpath:mock/dspace/rdf/collection.rdf")
    private Resource collectionRdf;

    @Value("classpath:mock/dspace/rdf/subcommunity.rdf")
    private Resource subcommunityRdf;

    @Value("classpath:mock/dspace/rdf/community.rdf")
    private Resource communityRdf;

    @Value("classpath:mock/dspace/json/collection.json")
    private Resource collection;

    @Value("classpath:mock/dspace/json/collections.json")
    private Resource collections;

    @Before
    public void setup() {
        setup(dSpaceCollectionManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(eq(DSPACE_URL + "/rdf/handle/123456789/158299"))).thenReturn(FileUtils.readFileToString(collectionRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(DSPACE_URL + "/rdf/handle/123456789/158301"))).thenReturn(FileUtils.readFileToString(subcommunityRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(DSPACE_URL + "/rdf/handle/123456789/158298"))).thenReturn(FileUtils.readFileToString(communityRdf.getFile(), "UTF-8"));
        String collectionManifest = dSpaceCollectionManifestService.getManifest(ManifestRequest.of("123456789/158299", false));

        Assert.assertEquals(objectMapper.readValue(collection.getFile(), JsonNode.class), objectMapper.readValue(collectionManifest, JsonNode.class));

        String collectionsManifest = dSpaceCollectionManifestService.getManifest(ManifestRequest.of("123456789/158298", false));

        Assert.assertEquals(objectMapper.readValue(collections.getFile(), JsonNode.class), objectMapper.readValue(collectionsManifest, JsonNode.class));
    }

}
