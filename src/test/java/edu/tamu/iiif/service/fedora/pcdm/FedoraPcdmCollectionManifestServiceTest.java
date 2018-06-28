package edu.tamu.iiif.service.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmCollectionManifestService;

public class FedoraPcdmCollectionManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmCollectionManifestService fedoraPcdmCollectionManifestService;

    @Value("classpath:mock/fedora/rdf/pcdm_collection_container.rdf")
    private Resource rdf;

    @Value("classpath:mock/fedora/json/collection.json")
    private Resource collection;

    @Before
    public void setup() {
        setup(fedoraPcdmCollectionManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(eq(PCDM_RDF_URL), any(String.class))).thenReturn(readFileToString(rdf.getFile(), "UTF-8"));
        String manifest = fedoraPcdmCollectionManifestService.getManifest(ManifestRequest.of("cars_pcdm", false));

        assertEquals(objectMapper.readValue(collection.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
