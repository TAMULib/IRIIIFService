package edu.tamu.iiif.service;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.ProtocolException;
import org.apache.http.message.BasicHttpRequest;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class CustomRedirectStrategyTest {

    @Test
    public void testGetLocationURI() throws ProtocolException, MalformedURLException {
        CustomRedirectStrategy customRedirectStrategy = new CustomRedirectStrategy();
        HttpRequest request = new BasicHttpRequest("GET", "http://localhost:9000/test");
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.PERMANENT_REDIRECT.value(), "Gone fishing!");
        response.setHeader("location", "http://localhost:9001/relocated/test");
        HttpContext context = new BasicHttpContext();

        URI uri = customRedirectStrategy.getLocationURI(request, response, context);

        assertEquals("http://localhost:9001/relocated/test", uri.toURL().toString());
    }

    @Test(expected = IllegalStateException.class)
    public void testGetLocationURIWithBadLocation() throws ProtocolException {
        CustomRedirectStrategy customRedirectStrategy = new CustomRedirectStrategy();
        HttpRequest request = new BasicHttpRequest("GET", "http://localhost:9000/test");
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.PERMANENT_REDIRECT.value(), "Gone fishing!");
        response.setHeader("location", "this$won't&work");
        HttpContext context = new BasicHttpContext();

        customRedirectStrategy.getLocationURI(request, response, context);

    }

    @Test(expected = ProtocolException.class)
    public void testGetLocationURIWithoutLocation() throws ProtocolException {
        CustomRedirectStrategy customRedirectStrategy = new CustomRedirectStrategy();
        HttpRequest request = new BasicHttpRequest("GET", "http://localhost:9000/test");
        HttpResponse response = new BasicHttpResponse(HttpVersion.HTTP_1_1, HttpStatus.PERMANENT_REDIRECT.value(), "Gone fishing!");
        HttpContext context = new BasicHttpContext();

        customRedirectStrategy.getLocationURI(request, response, context);

    }

}
