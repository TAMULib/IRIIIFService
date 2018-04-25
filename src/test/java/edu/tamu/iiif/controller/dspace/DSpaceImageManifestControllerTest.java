package edu.tamu.iiif.controller.dspace;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import org.apache.commons.io.FileUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import edu.tamu.iiif.service.dspace.DSpaceImageManifestService;

@RunWith(SpringRunner.class)
@WebMvcTest(value = DSpaceImageManifestController.class, secure = false)
public class DSpaceImageManifestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private DSpaceImageManifestService dSpaceImageManifestService;

    @Value("classpath:mock/dspace/json/image.json")
    private Resource json;

    @Test
    public void testGetImageManifest() throws Exception {
        String expected = FileUtils.readFileToString(json.getFile(), "UTF-8");
        when(dSpaceImageManifestService.getManifest(any(String.class), any(Boolean.class))).thenReturn(expected);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get("/dspace/image?context=123456789/158308/1/sports-car-146873_960_20.png").accept(MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        JSONAssert.assertEquals(expected, result.getResponse().getContentAsString(), false);
    }

}
