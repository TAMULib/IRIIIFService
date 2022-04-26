package edu.tamu.iiif.service.fedora.pcdm;

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
public class FedoraPcdmCollectionManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmCollectionManifestService fedoraPcdmCollectionManifestService;

    @Value("classpath:mock/fedora/rdf/collection_container.rdf")
    private Resource collectionRdf;

    @Value("classpath:mock/fedora/rdf/item_container.rdf")
    private Resource itemRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files.rdf")
    private Resource itemFilesRdf;

    @Value("classpath:mock/fedora/rdf/item_container_files_entry.rdf")
    private Resource itemFilesEntryRdf;

    @Value("classpath:mock/fedora/json/collection.json")
    private Resource collection;

    @BeforeEach
    public void setup() {
        setup(fedoraPcdmCollectionManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog"), eq(String.class))).thenReturn(readFileToString(collectionRdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084"), eq(String.class))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));
        when(restTemplate.getForObject(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/fcr:metadata"), eq(String.class))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));

        String manifest = fedoraPcdmCollectionManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog", false));
        assertEquals(objectMapper.readValue(collection.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
