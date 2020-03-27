package edu.tamu.iiif.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doThrow;
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
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.service.ResourceResolver;

@RunWith(SpringRunner.class)
@WebMvcTest(value = ResourceController.class, secure = false)
@AutoConfigureRestDocs(outputDir = "target/generated-snippets")
public class ResourceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ResourceResolver resourceResolver;

    private final RedisResource mockResource = new RedisResource("26f9b338-f744-11e8-8eb2-f2801f1b9fd1", "http://localhost:9000/fcrepo/rest/image01");

    private final RedisResource mockResourceNotExist = new RedisResource("26f9b338-f744-11e8-8eb2-f2801f1b9fd9", "http://localhost:9000/fcrepo/rest/image02");

    private final RedisResource mockResourceNotExistYet = new RedisResource("26f9b338-f744-11e8-8eb2-f2801f1b9fe3", "http://localhost:9000/fcrepo/rest/image03");

    private final String resourceWithIdNotFound = String.format("Resource with id %s not found!", mockResourceNotExist.getId());

    private final String resourceWithUrlNotFound = String.format("Resource with url %s not found!", mockResourceNotExist.getUrl());

    private final String resourceWithUrlNotFoundYet = String.format("Resource with url %s not found!", mockResourceNotExistYet.getUrl());

    @Before
    public void setup() throws InvalidUrlException, NotFoundException {
        try {
            when(resourceResolver.resolve(mockResource.getId())).thenReturn(mockResource.getUrl());
            when(resourceResolver.lookup(mockResource.getUrl())).thenReturn(mockResource.getId());
            when(resourceResolver.create(mockResource.getUrl())).thenReturn(mockResource.getId());
            when(resourceResolver.create(mockResourceNotExistYet.getUrl())).thenReturn(mockResourceNotExistYet.getId());
        } catch (InvalidUrlException e) {
            e.printStackTrace();
        } catch (NotFoundException e) {
            e.printStackTrace();
        }

        when(resourceResolver.resolve(mockResourceNotExist.getId())).thenThrow(new NotFoundException(resourceWithIdNotFound));

        when(resourceResolver.lookup(mockResourceNotExist.getUrl())).thenThrow(new NotFoundException(resourceWithUrlNotFound));

        when(resourceResolver.lookup(mockResourceNotExistYet.getUrl())).thenThrow(new NotFoundException(resourceWithUrlNotFoundYet));
        
        doThrow(new NotFoundException(resourceWithIdNotFound)).when(resourceResolver).remove(mockResourceNotExist.getId());
    }

    @Test
    public void testGetResourceUrl() throws Exception {
        RequestBuilder requestBuilder = get("/resources/{id}", mockResource.getId()).accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/getResourceUrl", pathParameters(parameterWithName("id").description("The resource id.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(mockResource.getUrl(), result.getResponse().getContentAsString());
    }

    @Test
    public void testGetResourceUrlNotFound() throws Exception {
        RequestBuilder requestBuilder = get("/resources/{id}", mockResourceNotExist.getId()).accept(TEXT_PLAIN);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(404, result.getResponse().getStatus());
        assertEquals(resourceWithIdNotFound, result.getResponse().getContentAsString());
    }

    @Test
    public void testRedirectToResource() throws Exception {
        RequestBuilder requestBuilder = get("/resources/{id}/redirect", mockResource.getId()).accept(APPLICATION_JSON);
        RestDocumentationResultHandler restDocHandler = document("resources/redirectToResource", pathParameters(parameterWithName("id").description("The resource id.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(301, result.getResponse().getStatus());
        assertEquals(mockResource.getUrl(), result.getResponse().getHeader("location"));
    }

    @Test
    public void testRedirectToResourceNotFound() throws Exception {
        RequestBuilder requestBuilder = get("/resources/{id}/redirect", mockResourceNotExist.getId()).accept(APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(404, result.getResponse().getStatus());
        assertEquals(resourceWithIdNotFound, result.getResponse().getContentAsString());
    }

    @Test
    public void testGetResourceId() throws Exception {
        RequestBuilder requestBuilder = get("/resources/lookup").param("uri", mockResource.getUrl()).accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/getResourceId", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(mockResource.getId(), result.getResponse().getContentAsString());
    }

    @Test
    public void testGetResourceIdInvalidUrl() throws Exception {
        RequestBuilder requestBuilder = get("/resources/lookup").param("uri", "fubar").accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/getResourceId", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        assertEquals("fubar is not a valid URL!", result.getResponse().getContentAsString());
    }

    @Test
    public void testGetResourceIdNotFound() throws Exception {
        RequestBuilder requestBuilder = get("/resources/lookup").param("uri", mockResourceNotExist.getUrl()).accept(TEXT_PLAIN);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(404, result.getResponse().getStatus());
        assertEquals(resourceWithUrlNotFound, result.getResponse().getContentAsString());
    }

    @Test
    public void testPutResource() throws Exception {
        RequestBuilder requestBuilder = put("/resources").param("uri", mockResourceNotExistYet.getUrl()).accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/putResource", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(201, result.getResponse().getStatus());
        assertEquals(mockResourceNotExistYet.getId(), result.getResponse().getContentAsString());
    }

    @Test
    public void testPutExistingResource() throws Exception {
        RequestBuilder requestBuilder = put("/resources").param("uri", mockResource.getUrl()).accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/putResource", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(mockResource.getId(), result.getResponse().getContentAsString());
    }

    @Test
    public void testPostResource() throws Exception {
        RequestBuilder requestBuilder = post("/resources").param("uri", mockResourceNotExistYet.getUrl()).accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/postResource", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(201, result.getResponse().getStatus());
        assertEquals(mockResourceNotExistYet.getId(), result.getResponse().getContentAsString());
    }

    @Test
    public void testPostExistingResource() throws Exception {
        RequestBuilder requestBuilder = post("/resources").param("uri", mockResource.getUrl()).accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/postResource", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(200, result.getResponse().getStatus());
        assertEquals(mockResource.getId(), result.getResponse().getContentAsString());
    }

    @Test
    public void testPostResourceInvalidUrl() throws Exception {
        RequestBuilder requestBuilder = post("/resources").param("uri", "fubar").accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/postResource", requestParameters(parameterWithName("uri").description("The resource URI.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(400, result.getResponse().getStatus());
        assertEquals("fubar is not a valid URL!", result.getResponse().getContentAsString());
    }

    @Test
    public void testRemoveResource() throws Exception {
        RequestBuilder requestBuilder = delete("/resources/{id}", mockResource.getId()).accept(TEXT_PLAIN);
        RestDocumentationResultHandler restDocHandler = document("resources/removeResource", pathParameters(parameterWithName("id").description("The resource id.")));
        MvcResult result = mockMvc.perform(requestBuilder).andDo(restDocHandler).andReturn();
        assertEquals(204, result.getResponse().getStatus());
    }

    @Test
    public void testRemoveResourceNotFound() throws Exception {
        RequestBuilder requestBuilder = delete("/resources/{id}", mockResourceNotExist.getId()).accept(TEXT_PLAIN);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        assertEquals(404, result.getResponse().getStatus());
    }

}
