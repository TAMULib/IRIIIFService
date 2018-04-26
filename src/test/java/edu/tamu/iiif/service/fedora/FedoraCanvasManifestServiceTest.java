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

public class FedoraCanvasManifestServiceTest extends AbstractFedoraManifestServiceTest {

    @InjectMocks
    private FedoraCanvasManifestService fedoraCanvasManifestService;

    @Value("classpath:mock/fedora/rdf/pcdm_item_container.rdf")
    private Resource rdf;

    @Value("classpath:mock/fedora/json/image0.json")
    private Resource image0;

    @Value("classpath:mock/fedora/json/canvas.json")
    private Resource canvas;

    @Before
    public void setup() {
        ReflectionTestUtils.setField(fedoraCanvasManifestService, "iiifServiceUrl", IIIF_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraCanvasManifestService, "imageServerUrl", IMAGE_SERVICE_URL);
        ReflectionTestUtils.setField(fedoraCanvasManifestService, "logoUrl", LOGO_URL);

        ReflectionTestUtils.setField(fedoraCanvasManifestService, "fedoraUrl", FEDORA_URL);
        ReflectionTestUtils.setField(fedoraCanvasManifestService, "pcdmRdfExtUrl", PCDM_RDF_URL);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(eq(PCDM_RDF_URL), any(String.class))).thenReturn(FileUtils.readFileToString(rdf.getFile(), "UTF-8"));
        when(httpService.get(any(String.class))).thenReturn(FileUtils.readFileToString(image0.getFile(), "UTF-8"));
        String manifest = fedoraCanvasManifestService.getManifest("cars_pcdm_objects/chevy/pages/page_0", false);
        Assert.assertEquals(objectMapper.readValue(canvas.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
