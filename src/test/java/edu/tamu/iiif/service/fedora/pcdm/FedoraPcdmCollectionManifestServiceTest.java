package edu.tamu.iiif.service.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
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

    @Before
    public void setup() {
        setup(fedoraPcdmCollectionManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog"))).thenReturn(readFileToString(collectionRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084"))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/fcr:metadata"))).thenReturn(readFileToString(itemRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/files/fcr:metadata"))).thenReturn(readFileToString(itemFilesRdf.getFile(), "UTF-8"));
        when(httpService.get(eq(FEDORA_URL + "/mwbObjects/TGWCatalog/Pages/ExCat0084/files/ExCat0084.jpg/fcr:metadata"))).thenReturn(readFileToString(itemFilesEntryRdf.getFile(), "UTF-8"));

        String manifest = fedoraPcdmCollectionManifestService.getManifest(ManifestRequest.of("mwbObjects/TGWCatalog", false));
        assertEquals(objectMapper.readValue(collection.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
