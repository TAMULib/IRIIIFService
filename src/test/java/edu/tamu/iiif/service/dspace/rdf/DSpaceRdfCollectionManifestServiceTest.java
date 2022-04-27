package edu.tamu.iiif.service.dspace.rdf;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;

@ExtendWith(MockitoExtension.class)
public class DSpaceRdfCollectionManifestServiceTest extends AbstractDSpaceRdfManifestServiceTest {

    @InjectMocks
    private DSpaceRdfCollectionManifestService dspaceRdfCollectionManifestService;

    @Value("classpath:mock/dspace/rdf/collection.rdf")
    private Resource collectionRdf;

    @Value("classpath:mock/dspace/rdf/subcommunity.rdf")
    private Resource subcommunityRdf;

    @Value("classpath:mock/dspace/rdf/community.rdf")
    private Resource communityRdf;

    @Value("classpath:mock/dspace/json/collection.json")
    private Resource collection;

    @Value("classpath:mock/dspace/rdf/item.rdf")
    private Resource itemRdf;

    @Value("classpath:mock/dspace/json/collections.json")
    private Resource collections;

    @BeforeEach
    public void setup() {
        setup(dspaceRdfCollectionManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(restTemplate.getForObject(eq(DSPACE_URL + "/rdf/handle/123456789/158299"), eq(String.class))).thenReturn(readFileToString(collectionRdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(DSPACE_URL + "/rdf/handle/123456789/158301"), eq(String.class))).thenReturn(readFileToString(subcommunityRdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(DSPACE_URL + "/rdf/handle/123456789/158302"), eq(String.class))).thenReturn(readFileToString(communityRdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(DSPACE_URL + "/rdf/handle/123456789/158298"), eq(String.class))).thenReturn(readFileToString(communityRdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(DSPACE_URL + "/rdf/handle/123456789/158308"), eq(String.class))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));

        String collectionManifest = dspaceRdfCollectionManifestService.getManifest(ManifestRequest.of("123456789/158299", false));

        assertEquals(objectMapper.readValue(collection.getFile(), JsonNode.class), objectMapper.readValue(collectionManifest, JsonNode.class));

        String collectionsManifest = dspaceRdfCollectionManifestService.getManifest(ManifestRequest.of("123456789/158298", false));

        assertEquals(objectMapper.readValue(collections.getFile(), JsonNode.class), objectMapper.readValue(collectionsManifest, JsonNode.class));
    }

}
