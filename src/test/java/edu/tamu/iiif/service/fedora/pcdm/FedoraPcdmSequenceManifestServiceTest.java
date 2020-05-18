package edu.tamu.iiif.service.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;

import com.fasterxml.jackson.databind.JsonNode;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;

import edu.tamu.iiif.controller.ManifestRequest;

public class FedoraPcdmSequenceManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmSequenceManifestService fedoraPcdmSequenceManifestService;

    @Value("classpath:mock/fedora/rdf/item_container.rdf")
    private Resource itemRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files.rdf")
    private Resource itemFilesRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files_entry.rdf")
    private Resource itemFilesEntryRdf;

    @Value("classpath:mock/fedora/json/image.json")
    private Resource image;

    @Value("classpath:mock/fedora/json/sequence.json")
    private Resource sequence;

    @Before
    public void setup() {
        setup(fedoraPcdmSequenceManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "image/png; charset=utf-8");
        when(restTemplate.headForHeaders(any(String.class))).thenReturn(headers);
        when(restTemplate.getForObject(any(String.class), eq(String.class))).thenReturn(readFileToString(image.getFile(), "UTF-8"));

        when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084"), eq(String.class))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/files/fcr:metadata"), eq(String.class))).thenReturn(readFileToString(itemFilesRdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/files/ExCat0084.jpg/fcr:metadata"), eq(String.class))).thenReturn(readFileToString(itemFilesEntryRdf.getFile(), "UTF-8"));

        String manifest = fedoraPcdmSequenceManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog/Pages/ExCat0084", false));
        assertEquals(objectMapper.readValue(sequence.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
