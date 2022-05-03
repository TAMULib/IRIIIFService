package edu.tamu.iiif.controller.advice;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.io.IOException;

import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.iiif.exception.NotFoundException;

@ExtendWith(SpringExtension.class)
public class GlobalExceptionHandlerTest {

    private GlobalExceptionHandler globalExceptionHandler = new GlobalExceptionHandler();

    @Test
    public void testHandleIOException() {
        String message = "This is a test";
        ResponseEntity<String> response = globalExceptionHandler.handleIOException(new IOException(message));
        assertEquals(message, response.getBody());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    public void testHandleIOExceptionIOException() {
        ResponseEntity<String> response = globalExceptionHandler.handleIOException(new IOException(new Throwable("Broken pipe")));
        assertNull(response);
    }

    @Test
    public void testHandleRiotException() {
        String message = "This is a test";
        ResponseEntity<String> response = globalExceptionHandler.handleRiotException(new RiotException(message));
        assertEquals(message, response.getBody());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    public void testHandleRedisConnectionFailureException() {
        String message = "This is a test";
        ResponseEntity<String> response = globalExceptionHandler.handleRedisConnectionFailureException(new RedisConnectionFailureException(message));
        assertEquals(message, response.getBody());
        assertEquals(HttpStatus.SERVICE_UNAVAILABLE, response.getStatusCode());
    }

    @Test
    public void testHandleNotFoundException() {
        String message = "This is a test";
        ResponseEntity<String> response = globalExceptionHandler.handleNotFoundException(new NotFoundException(message));
        assertEquals(message, response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

}
