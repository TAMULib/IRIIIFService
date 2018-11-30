package edu.tamu.iiif.service.fedora.pcdm;

import static edu.tamu.iiif.model.ManifestType.PRESENTATION;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;

import com.fasterxml.jackson.databind.JsonNode;

import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.model.ManifestType;
import edu.tamu.iiif.model.RedisManifest;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmPresentationManifestService;

public class FedoraPcdmPresentationManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmPresentationManifestService fedoraPcdmPresentationManifestService;

    @Value("classpath:mock/fedora/rdf/pcdm_item_container.rdf")
    private Resource rdf;

    @Value("classpath:mock/fedora/rdf/pcdm_item_container_without_order.rdf")
    private Resource rdfWithoutOrder;

    @Value("classpath:mock/fedora/rdf/pcdm_item_proxy_0.rdf")
    private Resource proxy0Rdf;

    @Value("classpath:mock/fedora/rdf/pcdm_item_proxy_1.rdf")
    private Resource proxy1Rdf;

    @Value("classpath:mock/fedora/rdf/pcdm_item_proxy_2.rdf")
    private Resource proxy2Rdf;

    @Value("classpath:mock/fedora/json/image0.json")
    private Resource image0;

    @Value("classpath:mock/fedora/json/image1.json")
    private Resource image1;

    @Value("classpath:mock/fedora/json/image2.json")
    private Resource image2;

    @Value("classpath:mock/fedora/json/presentation.json")
    private Resource presentation;

    @Before
    public void setup() {
        setup(fedoraPcdmPresentationManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        setupMocks();
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestAllowed() throws IOException, URISyntaxException {
        setupMocks();
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false, Arrays.asList(new String[] { "image/png", "image/jpeg" }), Arrays.asList(new String[] {})));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestDisallowed() throws IOException, URISyntaxException {
        setupMocks();
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false, Arrays.asList(new String[] {}), Arrays.asList(new String[] { "image/bmp", "image/jpeg" })));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestCached() throws IOException, URISyntaxException {
        RedisManifest redisManifest = new RedisManifest("cars_pcdm_objects/chevy", PRESENTATION, FEDORA_PCDM_IDENTIFIER, readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestUpdateCached() throws IOException, URISyntaxException {
        setupMocks();
        RedisManifest redisManifest = new RedisManifest("cars_pcdm_objects/chevy", PRESENTATION, FEDORA_PCDM_IDENTIFIER, readFileToString(presentation.getFile(), "UTF-8"));
        when(redisManifestRepo.findByPathAndTypeAndRepositoryAndAllowedAndDisallowed(any(String.class), any(ManifestType.class), any(String.class), any(String.class), any(String.class))).thenReturn(Optional.of(redisManifest));
        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", true));
        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    @Test
    public void testGetManifestWithoutOrder() throws IOException, URISyntaxException {
        when(httpService.get(eq(PCDM_RDF_URL), any(String.class))).thenReturn(readFileToString(rdfWithoutOrder.getFile(), "UTF-8"));

        when(httpService.contentType(any(String.class))).thenReturn("image/png; charset=utf-8");

        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8wL2ZpbGVzL2NhcjEuanBn/info.json"))).thenReturn(readFileToString(image0.getFile(), "UTF-8"));

        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8xL2ZpbGVzL2NhcjIuanBn/info.json"))).thenReturn(readFileToString(image1.getFile(), "UTF-8"));

        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8yL2ZpbGVzL2NhcjMuanBn/info.json"))).thenReturn(readFileToString(image2.getFile(), "UTF-8"));

        String manifest = fedoraPcdmPresentationManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false));

        assertEquals(objectMapper.readValue(presentation.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

    private void setupMocks() throws IOException {
        when(httpService.get(eq(PCDM_RDF_URL), any(String.class))).thenReturn(readFileToString(rdf.getFile(), "UTF-8"));

        when(httpService.contentType(any(String.class))).thenReturn("image/png; charset=utf-8");

        when(httpService.get(eq(FEDORA_URL + "/cars_pcdm_objects/chevy/orderProxies/page_0_proxy/fcr:metadata"))).thenReturn(readFileToString(proxy0Rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8wL2ZpbGVzL2NhcjEuanBn/info.json"))).thenReturn(readFileToString(image0.getFile(), "UTF-8"));

        when(httpService.get(eq(FEDORA_URL + "/cars_pcdm_objects/chevy/orderProxies/page_1_proxy/fcr:metadata"))).thenReturn(readFileToString(proxy1Rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8xL2ZpbGVzL2NhcjIuanBn/info.json"))).thenReturn(readFileToString(image1.getFile(), "UTF-8"));

        when(httpService.get(eq(FEDORA_URL + "/cars_pcdm_objects/chevy/orderProxies/page_2_proxy/fcr:metadata"))).thenReturn(readFileToString(proxy2Rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8yL2ZpbGVzL2NhcjMuanBn/info.json"))).thenReturn(readFileToString(image2.getFile(), "UTF-8"));
    }

}
