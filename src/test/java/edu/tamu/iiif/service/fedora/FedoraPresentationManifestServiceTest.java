package edu.tamu.iiif.service.fedora;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Optional;

import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.model.RepositoryType;

public class FedoraPresentationManifestServiceTest extends AbstractFedoraManifestServiceTest {

    @InjectMocks
    private FedoraPresentationManifestService fedoraPresentationManifestService;

    @Value("classpath:mock/fedora/rdf/pcdm_item_container.rdf")
    private Resource rdf;

    @Value("classpath:mock/fedora/rdf/pcdm_item_container_without_order.rdf")
    private Resource rdfWithoutOrder;

    @Value("classpath:mock/fedora/rdf/pcdm_item_proxy_0.rdf")
    private Resource proxy0Rdf;

    @Value("classpath:mock/fedora/rdf/pcdm_item_proxy_1.rdf")
    private Resource proxy1Rdf;

    @Value("classpath:mock/fedora/json/image0.json")
    private Resource image0;

    @Value("classpath:mock/fedora/json/image1.json")
    private Resource image1;

    @Value("classpath:mock/fedora/json/presentation.json")
    private Resource presentation;

    @Before
    public void setup() {
        setup(fedoraPresentationManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        setupMocks();
        String manifest = fedoraPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false));
        Assert.assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestCached() throws IOException, URISyntaxException {
        RedisManifest redisManifest = new RedisManifest("cars_pcdm_objects/chevy", ManifestType.PRESENTATION, RepositoryType.FEDORA, FileUtils.readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(RepositoryType.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = fedoraPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false));
        Assert.assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestUpdateCached() throws IOException, URISyntaxException {
        setupMocks();
        RedisManifest redisManifest = new RedisManifest("cars_pcdm_objects/chevy", ManifestType.PRESENTATION, RepositoryType.FEDORA, FileUtils.readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(RepositoryType.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = fedoraPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", true));
        Assert.assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestWithoutOrder() throws IOException, URISyntaxException {
        when(httpService.get(eq(PCDM_RDF_URL), any(String.class))).thenReturn(FileUtils.readFileToString(rdfWithoutOrder.getFile(), "UTF-8"));

        when(httpService.contentType(any(String.class))).thenReturn("image/png");

        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL2NoZXZ5L3BhZ2VzL3BhZ2VfMC9maWxlcy9QVEFSXzgwMHg0MDAucG5n/info.json"))).thenReturn(FileUtils.readFileToString(image0.getFile(), "UTF-8"));

        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL2NoZXZ5L3BhZ2VzL3BhZ2VfMS9maWxlcy9jYXIyLmpwZw==/info.json"))).thenReturn(FileUtils.readFileToString(image1.getFile(), "UTF-8"));

        String manifest = fedoraPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false));

        Assert.assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    private void setupMocks() throws IOException {
        when(httpService.get(eq(PCDM_RDF_URL), any(String.class))).thenReturn(FileUtils.readFileToString(rdf.getFile(), "UTF-8"));

        when(httpService.contentType(any(String.class))).thenReturn("image/png");

        when(httpService.get(eq(FEDORA_URL + "/cars_pcdm_objects/chevy/orderProxies/page_0_proxy/fcr:metadata"))).thenReturn(FileUtils.readFileToString(proxy0Rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL2NoZXZ5L3BhZ2VzL3BhZ2VfMC9maWxlcy9QVEFSXzgwMHg0MDAucG5n/info.json"))).thenReturn(FileUtils.readFileToString(image0.getFile(), "UTF-8"));

        when(httpService.get(eq(FEDORA_URL + "/cars_pcdm_objects/chevy/orderProxies/page_1_proxy/fcr:metadata"))).thenReturn(FileUtils.readFileToString(proxy1Rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhOmNhcnNfcGNkbV9vYmplY3RzL2NoZXZ5L3BhZ2VzL3BhZ2VfMS9maWxlcy9jYXIyLmpwZw==/info.json"))).thenReturn(FileUtils.readFileToString(image1.getFile(), "UTF-8"));
    }

}
