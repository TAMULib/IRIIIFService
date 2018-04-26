package edu.tamu.iiif.service;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicStatusLine;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
public class HttpServiceTest {

    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    private CloseableHttpResponse response;

    private HttpEntity entity;

    @InjectMocks
    private HttpService httpService;

    @Value("classpath:mock/fedora/json/image.json")
    private Resource image;

    @Value("classpath:mock/fedora/rdf/pcdm_collection_container.rdf")
    private Resource rdf;

    @Before
    public void setup() throws ClientProtocolException, IOException {
        MockitoAnnotations.initMocks(this);

        objectMapper = new ObjectMapper();

        httpClient = mock(CloseableHttpClient.class);

        response = mock(CloseableHttpResponse.class);

        entity = mock(HttpEntity.class);

        ReflectionTestUtils.setField(httpService, "connectionTimeout", 1000);
        ReflectionTestUtils.setField(httpService, "connectionRequestTimeout", 1000);
        ReflectionTestUtils.setField(httpService, "socketTimeout", 1000);
        ReflectionTestUtils.setField(httpService, "retries", 3);

        ReflectionTestUtils.setField(httpService, "httpClient", httpClient);

        Assert.assertNotNull(httpService);

        when(response.getEntity()).thenReturn(entity);

        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);

    }

    @Test
    public void testGet() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(entity.getContent()).thenReturn(image.getInputStream());
        String response = httpService.get("http://localhost:8182/iiif/2/test/info.json");
        Assert.assertEquals(objectMapper.readValue(image.getFile(), JsonNode.class), objectMapper.readValue(response, JsonNode.class));
    }

    @Test
    public void testGetIncorrectStatusCode() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "BAD REQUEST"));
        when(entity.getContent()).thenReturn(image.getInputStream());
        String response = httpService.get("http://localhost:8182/iiif/2/fail/info.json");
        Assert.assertNull(response);
    }

    @Test
    public void testGetMalformedUrl() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(entity.getContent()).thenReturn(image.getInputStream());
        String response = httpService.get("@foo://invalid.bar");
        Assert.assertNull(response);
    }

    @Test
    public void testGetWithContext() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(entity.getContent()).thenReturn(rdf.getInputStream());
        String response = httpService.get("http://localhost:9107", "pcdm_cars");
        Assert.assertEquals(FileUtils.readFileToString(rdf.getFile(), "UTF-8"), response);
    }

}
