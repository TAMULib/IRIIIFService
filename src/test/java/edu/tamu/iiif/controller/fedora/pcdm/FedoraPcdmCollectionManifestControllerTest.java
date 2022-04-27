package edu.tamu.iiif.controller.fedora.pcdm;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.io.Resource;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import edu.tamu.iiif.config.model.AdminConfig;
import edu.tamu.iiif.controller.AbstractManifestControllerTest;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.service.fedora.pcdm.FedoraPcdmCollectionManifestService;

@Import({ AdminConfig.class })
@ExtendWith(MockitoExtension.class)
@WebMvcTest(value = FedoraPcdmCollectionManifestController.class)
public class FedoraPcdmCollectionManifestControllerTest extends AbstractManifestControllerTest {

    @MockBean
    private FedoraPcdmCollectionManifestService fedoraPcdmCollectionManifestService;

    @Value("classpath:mock/fedora/json/collection.json")
    private Resource json;

    @Test
    public void testGetManifest() throws Exception {
        String expected = readFileToString(json.getFile(), "UTF-8");
        when(fedoraPcdmCollectionManifestService.getManifest(any(ManifestRequest.class))).thenReturn(expected);
        RequestBuilder requestBuilder = get("/fedora/collection/{context}", "cars_pcdm").accept(APPLICATION_JSON);
        RestDocumentationResultHandler restDocHandler = document("fedora/collection", pathParameters(parameterWithName("context").description("The context path.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());
    }

}
