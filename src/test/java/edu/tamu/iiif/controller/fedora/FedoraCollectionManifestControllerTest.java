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
import edu.tamu.iiif.service.fedora.FedoraCollectionManifestService;

@WebMvcTest(value = FedoraCollectionManifestController.class, secure = false)
public class FedoraCollectionManifestControllerTest extends AbstractManifestControllerTest {

    @MockBean
    private FedoraCollectionManifestService fedaorCollectionManifestService;

    @Value("classpath:mock/fedora/json/collection.json")
    private Resource json;

    @Test
    public void testGetManifest() throws Exception {
        String expected = FileUtils.readFileToString(json.getFile(), "UTF-8");
        when(fedaorCollectionManifestService.getManifest(any(ManifestRequest.class))).thenReturn(expected);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/fedora/collection?context=cars_pcdm").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

}
