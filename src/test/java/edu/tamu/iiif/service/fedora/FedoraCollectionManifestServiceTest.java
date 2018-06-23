package edu.tamu.iiif.service.fedora;

import static org.mockito.Matchers.any;
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

import edu.tamu.iiif.controller.ManifestRequest;

public class FedoraCollectionManifestServiceTest extends AbstractFedoraManifestServiceTest {

    @InjectMocks
    private FedoraCollectionManifestService fedoraCollectionManifestService;

    @Value("classpath:mock/fedora/rdf/pcdm_collection_container.rdf")
    private Resource rdf;

    @Value("classpath:mock/fedora/json/collection.json")
    private Resource collection;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(fedoraCollectionManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraCollectionManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraCollectionManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(fedoraCollectionManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraCollectionManifestService, "fedoraPcdmExtUrl", PCDM_RDF_URL);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(eq(PCDM_RDF_URL), any(String.class))).thenReturn(FileUtils.readFileToString(rdf.getFile(), "UTF-8"));

        String manifest = fedoraCollectionManifestService.getManifest(ManifestRequest.of("cars_pcdm", false));

        Assert.assertEquals(objectMapper.readValue(collection.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
