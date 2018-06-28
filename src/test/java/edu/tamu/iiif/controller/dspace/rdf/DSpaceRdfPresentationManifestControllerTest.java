package edu.tamu.iiif.controller.dspace.rdf;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import edu.tamu.iiif.controller.AbstractManifestControllerTest;
import edu.tamu.iiif.controller.ManifestRequest;
import edu.tamu.iiif.controller.dspace.rdf.DSpaceRdfPresentationManifestController;
import edu.tamu.iiif.service.dspace.rdf.DSpaceRdfPresentationManifestService;

@WebMvcTest(value = DSpaceRdfPresentationManifestController.class, secure = false)
public class DSpaceRdfPresentationManifestControllerTest extends AbstractManifestControllerTest {

    @MockBean
    private DSpaceRdfPresentationManifestService dspaceRdfPresentationManifestService;

    @Value("classpath:mock/dspace/json/presentation.json")
    private Resource json;

    @Test
    public void testGetManifest() throws Exception {
        String expected = readFileToString(json.getFile(), "UTF-8");
        when(dspaceRdfPresentationManifestService.getManifest(any(ManifestRequest.class))).thenReturn(expected);
        RequestBuilder requestBuilder = get("/" + dspaceRdfIdentifier + "/presentation/123456789/158308").accept(APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(expected, result.getResponse().getContentAsString());
    }

}
