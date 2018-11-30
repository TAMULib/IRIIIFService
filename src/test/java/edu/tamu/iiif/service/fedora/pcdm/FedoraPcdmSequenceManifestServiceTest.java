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
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmSequenceManifestService;

public class FedoraPcdmSequenceManifestServiceTest extends AbstractFedoraPcdmManifestServiceTest {

    @InjectMocks
    private FedoraPcdmSequenceManifestService fedoraPcdmSequenceManifestService;

    @Value("classpath:mock/fedora/rdf/pcdm_item_container.rdf")
    private Resource rdf;

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

    @Value("classpath:mock/fedora/json/sequence.json")
    private Resource sequence;

    @Before
    public void setup() {
        setup(fedoraPcdmSequenceManifestService);
    }

    @Test
    public void testGetManifest() throws IOException, URISyntaxException {
        when(httpService.get(eq(PCDM_RDF_URL), any(String.class))).thenReturn(readFileToString(rdf.getFile(), "UTF-8"));

        when(httpService.contentType(any(String.class))).thenReturn("image/png; charset=utf-8");

        when(httpService.get(eq(FEDORA_URL + "/cars_pcdm_objects/chevy/orderProxies/page_0_proxy/fcr:metadata"))).thenReturn(readFileToString(proxy0Rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8wL2ZpbGVzL2NhcjEuanBn/info.json"))).thenReturn(readFileToString(image0.getFile(), "UTF-8"));

        when(httpService.get(eq(FEDORA_URL + "/cars_pcdm_objects/chevy/orderProxies/page_1_proxy/fcr:metadata"))).thenReturn(readFileToString(proxy1Rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8xL2ZpbGVzL2NhcjIuanBn/info.json"))).thenReturn(readFileToString(image1.getFile(), "UTF-8"));

        when(httpService.get(eq(FEDORA_URL + "/cars_pcdm_objects/chevy/orderProxies/page_2_proxy/fcr:metadata"))).thenReturn(readFileToString(proxy2Rdf.getFile(), "UTF-8"));
        when(httpService.get(eq(IMAGE_SERVICE_URL + "/ZmVkb3JhLXBjZG06Y2Fyc19wY2RtX29iamVjdHMvY2hldnkvcGFnZXMvcGFnZV8yL2ZpbGVzL2NhcjMuanBn/info.json"))).thenReturn(readFileToString(image2.getFile(), "UTF-8"));

        String manifest = fedoraPcdmSequenceManifestService.getManifest(ManifestRequest.of("cars_pcdm_objects/chevy", false));

        assertEquals(objectMapper.readValue(sequence.getFile(), JsonNode.class), objectMapper.readValue(manifest, JsonNode.class));
    }

}
