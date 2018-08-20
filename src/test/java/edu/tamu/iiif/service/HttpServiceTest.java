package edu.tamu.iiif.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicStatusLine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RunWith(SpringRunner.class)
public class HttpServiceTest {

    private ObjectMapper objectMapper;

    private HttpClient httpClient;

    private CloseableHttpResponse response;

    private HttpEntity entity;

    private Header header;

    @InjectMocks
    private HttpService httpService;

    @Value("classpath:mock/fedora/json/image0.json")
    private Resource image0;

    @Value("classpath:mock/fedora/rdf/pcdm_collection_container.rdf")
    private Resource rdf;

    @Before
    public void setup() throws ClientProtocolException, IOException {
        initMocks(this);

        objectMapper = new ObjectMapper();

        httpClient = mock(CloseableHttpClient.class);

        response = mock(CloseableHttpResponse.class);

        entity = mock(HttpEntity.class);

        header = new BasicHeader("Content-Type", "image/jpeg");

        setField(httpService, "connectionTimeout", 1000);
        setField(httpService, "connectionRequestTimeout", 1000);
        setField(httpService, "socketTimeout", 1000);

        setField(httpService, "httpClient", httpClient);

        assertNotNull(httpService);

        when(response.getEntity()).thenReturn(entity);
        when(response.getFirstHeader("Content-Type")).thenReturn(header);

        when(httpClient.execute(any(HttpGet.class))).thenReturn(response);
    }

    @Test
    public void testGet() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(entity.getContent()).thenReturn(image0.getInputStream());
        String response = httpService.get("http://localhost:8182/iiif/2/test/info.json");
        assertEquals(objectMapper.readValue(image0.getFile(), JsonNode.class), objectMapper.readValue(response, JsonNode.class));
    }

    @Test
    public void testGetWithContext() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(entity.getContent()).thenReturn(rdf.getInputStream());
        String response = httpService.get("http://localhost:9107", "pcdm_cars");
        assertEquals(FileUtils.readFileToString(rdf.getFile(), "UTF-8"), response);
    }

    @Test
    public void testGetIncorrectStatusCode() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "BAD REQUEST"));
        when(entity.getContent()).thenReturn(image0.getInputStream());
        String response = httpService.get("http://localhost:8182/iiif/2/fail/info.json");
        assertNull(response);
    }

    @Test
    public void testGetMalformedUrl() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(entity.getContent()).thenReturn(image0.getInputStream());
        String response = httpService.get("@foo://invalid.bar");
        assertNull(response);
    }

    @Test
    public void testContentType() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        String response = httpService.contentType("https://brandguide.tamu.edu/assets/downloads/logos/TAM-Logo.png");
        assertEquals(header.getValue(), response);
    }

    @Test
    public void testContentTypeNoContentTypeHeader() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        when(response.getFirstHeader("Content-Type")).thenReturn(null);
        String response = httpService.contentType("https://brandguide.tamu.edu/assets/downloads/logos/TAM-Logo.png");
        assertNull(response);
    }

    @Test
    public void testContentTypeIncorrectStatusCode() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_BAD_REQUEST, "BAD REQUEST"));
        String response = httpService.contentType("https://brandguide.tamu.edu/assets/downloads/logos/TAM-Logo.png");
        assertNull(response);
    }

    @Test
    public void testContentTypeMalformedUrl() throws UnsupportedOperationException, IOException {
        when(response.getStatusLine()).thenReturn(new BasicStatusLine(HttpVersion.HTTP_1_1, HttpStatus.SC_OK, "OK"));
        String response = httpService.contentType("@foo://invalid.bar");
        assertNull(response);
    }

}
