package edu.tamu.iiif.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_PLAIN;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.restdocs.request.RequestDocumentation.requestParameters;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ResourceController.class, secure = false)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RedisResourceRepo redisResourceRepo;

    @Before
    public void setup() {
        List<RedisResource> mockResources = new ArrayList<RedisResource>();

        mockResources.add(new RedisResource("26f9b338-f744-11e8-8eb2-f2801f1b9fd1", "http://localhost:9000/fcrepo/rest/image01"));

        mockResources.add(new RedisResource("26f9b7a2-f744-11e8-8eb2-f2801f1b9fd1", "http://localhost:9000/fcrepo/rest/image02"));

        mockResources.add(new RedisResource("26f9b900-f744-11e8-8eb2-f2801f1b9fd1", "http://localhost:9000/xmlui/bitstream/handle/123456789/158308/image03.jpg"));

        mockResources.add(new RedisResource("26f9ba2c-f744-11e8-8eb2-f2801f1b9fd1", "http://localhost:9000/xmlui/bitstream/handle/123456789/158308/image04.jpg"));

        mockResources.forEach(mockResource -> {
            try {
                when(redisResourceRepo.getOrCreate(mockResource.getUrl())).thenReturn(mockResource);
                when(redisResourceRepo.findOne(mockResource.getId())).thenReturn(mockResource);
                when(redisResourceRepo.findByUrl(mockResource.getUrl())).thenReturn(Optional.ofNullable(mockResource));
            } catch (InvalidUrlException e) {
                e.printStackTrace();
            }
        });

        when(redisResourceRepo.findAll()).thenReturn(mockResources);
        when(redisResourceRepo.findByUrl("http://localhost:9000/fcrepo/rest/image09")).thenReturn(Optional.empty());
    }

    @Test
    public void testGetResourceUrl() throws Exception {
        RequestBuilder requestBuilder = get("/resources/{id}", "26f9b338-f744-11e8-8eb2-f2801f1b9fd1").accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/getResourceUrl", pathParameters(parameterWithName("id").description("The resource id.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("http://localhost:9000/fcrepo/rest/image01", result.getResponse().getContentAsString());
    }

    @Test
    public void testGetResourceUrlNotFound() throws Exception {
        RequestBuilder requestBuilder = get("/resources/{id}", "26f9b338-f744-11e8-8eb2-f2801f1b9fd8").accept(TEXT_PLAIN);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(404, result.getResponse().getStatus());
        assertEquals("Unable to resolve resource with id 26f9b338-f744-11e8-8eb2-f2801f1b9fd8", result.getResponse().getContentAsString());
    }

    @Test
    public void testRedirectToResource() throws Exception {
        RequestBuilder requestBuilder = get("/resources/{id}/redirect", "26f9b338-f744-11e8-8eb2-f2801f1b9fd1").accept(APPLICATION_JSON);
        RestDocumentationResultHandler restDocHandler = document("resources/redirectToResource", pathParameters(parameterWithName("id").description("The resource id.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(301, result.getResponse().getStatus());
        assertEquals("http://localhost:9000/fcrepo/rest/image01", result.getResponse().getHeader("location"));
    }

    @Test
    public void testRedirectToResourceNotFound() throws Exception {
        RequestBuilder requestBuilder = get("/resources/{id}/redirect", "26f9b338-f744-11e8-8eb2-f2801f1b9fd9").accept(APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(404, result.getResponse().getStatus());
        assertEquals("Unable to resolve resource with id 26f9b338-f744-11e8-8eb2-f2801f1b9fd9", result.getResponse().getContentAsString());
    }

    @Test
    public void testGetResourceId() throws Exception {
        RequestBuilder requestBuilder = get("/resources/lookup").param("uri", "http://localhost:9000/fcrepo/rest/image02").accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/getResourceId", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("26f9b7a2-f744-11e8-8eb2-f2801f1b9fd1", result.getResponse().getContentAsString());
    }

    @Test
    public void testGetResourceIdNotFound() throws Exception {
        RequestBuilder requestBuilder = get("/resources/lookup").param("uri", "http://localhost:9000/fcrepo/rest/image09").accept(TEXT_PLAIN);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(404, result.getResponse().getStatus());
        assertEquals("No resourse found with uri http://localhost:9000/fcrepo/rest/image09", result.getResponse().getContentAsString());
    }

    @Test
    public void testPutResource() throws Exception {
        RequestBuilder requestBuilder = put("/resources").param("uri", "http://localhost:9000/xmlui/bitstream/handle/123456789/158308/image03.jpg").accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/putResource", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(201, result.getResponse().getStatus());
        assertEquals("26f9b900-f744-11e8-8eb2-f2801f1b9fd1", result.getResponse().getContentAsString());
    }

    @Test
    public void testPostResource() throws Exception {
        RequestBuilder requestBuilder = post("/resources").param("uri", "http://localhost:9000/xmlui/bitstream/handle/123456789/158308/image03.jpg").accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/postResource", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(201, result.getResponse().getStatus());
        assertEquals("26f9b900-f744-11e8-8eb2-f2801f1b9fd1", result.getResponse().getContentAsString());
    }

    @Test
    public void testRemoveResource() throws Exception {
        RequestBuilder requestBuilder = delete("/resources/{id}", "26f9b338-f744-11e8-8eb2-f2801f1b9fd1").accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/removeResource", pathParameters(parameterWithName("id").description("The resource id.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals("Success", result.getResponse().getContentAsString());
    }

    @Test
    public void testRemoveResourceNotFound() throws Exception {
        RequestBuilder requestBuilder = delete("/resources/{id}", "26f9b338-f744-11e8-8eb2-f2801f1b9fd7").accept(TEXT_PLAIN);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(404, result.getResponse().getStatus());
    }

}
