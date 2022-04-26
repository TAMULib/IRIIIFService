package edu.tamu.iiif.service.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
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
import org.springframework.http.HttpHeaders;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;

@ExtendWith(MockitoExtension.class)
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

    @BeforeEach
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
