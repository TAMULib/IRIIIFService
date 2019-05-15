package edu.tamu.iiif.controller.dspace.rdf;

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
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfCanvasManifestService;

@WebMvcTest(value = DSpaceRdfCanvasManifestController.class, secure = false)
public class DSpaceRdfCanvasManifestControllerTest extends AbstractManifestControllerTest {

    @MockBean
    private DSpaceRdfCanvasManifestService dspaceRdfCanvasManifestService;

    @Value("classpath:mock/dspace/json/canvas.json")
    private Resource json;

    @Test
    public void testGetManifest() throws Exception {
        String expected = readFileToString(json.getFile(), "UTF-8");
        when(dspaceRdfCanvasManifestService.getManifest(any(ManifestRequest.class))).thenReturn(expected);
        RequestBuilder requestBuilder = get("/dspace/canvas/{context}", "123456789/158308/1/sports-car-146873_960_720.png").accept(APPLICATION_JSON);
        RestDocumentationResultHandler restDocHandler = document("dspace/canvas", pathParameters(parameterWithName("context").description("The context path.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());
    }

}
