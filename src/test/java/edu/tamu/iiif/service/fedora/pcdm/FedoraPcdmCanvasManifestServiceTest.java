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

public class FedoraPcdmCanvasManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmCanvasManifestService fedoraPcdmCanvasManifestService;

    @Value("classpath:mock/fedora/rdf/item_container.rdf")
    private Resource itemRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files.rdf")
    private Resource itemFilesRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files_entry.rdf")
    private Resource itemFilesEntryRdf;

    @Value("classpath:mock/fedora/json/image0.json")
    private Resource image0;

    @Value("classpath:mock/fedora/json/canvas.json")
    private Resource canvas;

    @Before
    public void setup() {
        setup(fedoraPcdmCanvasManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {

        when(httpService.contentType(any(String.class))).thenReturn("image/png; charset=utf-8");
        when(httpService.get(any(String.class))).thenReturn(readFileToString(image0.getFile(), "UTF-8"));

        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084"))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/files/fcr:metadata"))).thenReturn(readFileToString(itemFilesRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/files/ExCat0084.jpg/fcr:metadata"))).thenReturn(readFileToString(itemFilesEntryRdf.getFile(), "UTF-8"));

        String manifest = fedoraPcdmCanvasManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog/Pages/ExCat0084", false));
        assertEquals(objectMapper.readValue(canvas.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
