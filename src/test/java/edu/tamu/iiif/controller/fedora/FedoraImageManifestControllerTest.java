package edu.tamu.iiif.controller.fedora;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import edu.tamu.iiif.controller.AbstractManifestControllerTest;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.fedora.FedoraImageManifestService;

@WebMvcTest(value = FedoraImageManifestController.class, secure = false)
public class FedoraImageManifestControllerTest extends AbstractManifestControllerTest {

    @MockBean
    private FedoraImageManifestService fedaorImageManifestService;

    @Value("classpath:mock/fedora/json/image0.json")
    private Resource image0;

    @Test
    public void testGetManifest() throws Exception {
        String expected = FileUtils.readFileToString(image0.getFile(), "UTF-8");
        when(fedaorImageManifestService.getManifest(any(ManifestRequest.class))).thenReturn(expected);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/" + fedoraPcdmIdentifier + "/image/cars_pcdm_objects/chevy/pages/page_0/files/PTAR_800x400.png").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

}
