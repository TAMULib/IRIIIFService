package edu.tamu.iiif.controller.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import edu.tamu.iiif.controller.AbstractManifestControllerTest;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmImageManifestService;

@WebMvcTest(value = FedoraPcdmImageManifestController.class, secure = false)
public class FedoraPcdmImageManifestControllerTest extends AbstractManifestControllerTest {

    @MockBean
    private FedoraPcdmImageManifestService fedoraPcdmImageManifestService;

    @Value("classpath:mock/fedora/json/image.json")
    private Resource image0;

    @Test
    public void testGetManifest() throws Exception {
        String expected = readFileToString(image0.getFile(), "UTF-8");
        when(fedoraPcdmImageManifestService.getManifest(any(ManifestRequest.class))).thenReturn(expected);
        RequestBuilder requestBuilder = get("/fedora/image/{context}", "cars_pcdm_objects/chevy/pages/page_0/files/PTAR_800x400.png").accept(APPLICATION_JSON);
        RestDocumentationResultHandler restDocHandler = document("fedora/image", pathParameters(parameterWithName("context").description("The context path.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());
    }

}
