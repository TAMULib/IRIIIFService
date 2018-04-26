package edu.tamu.iiif.controller.advice;

import java.io.IOException;

import org.apache.jena.riot.RiotException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.exception.NotFoundException;

@RunWith(SpringRunner.class)
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleIOException() {
        ResponseEntity<String> response = globalExceptionHandler.handleIOException(new IOException("This is a test"));
        Assert.assertEquals("This is a test", response.getBody());
        Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    public void testHandleIOExceptionIOException() {
        ResponseEntity<String> response = globalExceptionHandler.handleIOException(new IOException(new Throwable("Broken pipe")));
        Assert.assertNull(response);
    }

    @Test
    public void testHandleRiotException() {
        ResponseEntity<String> response = globalExceptionHandler.handleRiotException(new RiotException("This is a test"));
        Assert.assertEquals("This is a test", response.getBody());
        Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    public void testHandleRedisConnectionFailureException() {
        ResponseEntity<String> response = globalExceptionHandler.handleRedisConnectionFailureException(new RedisConnectionFailureException("This is a test"));
        Assert.assertEquals("This is a test", response.getBody());
        Assert.assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    public void testHandleNotFoundException() {
        ResponseEntity<String> response = globalExceptionHandler.handleNotFoundException(new NotFoundException("This is a test"));
        Assert.assertEquals("This is a test", response.getBody());
        Assert.assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
