package edu.tamu.iiif.controller.advice;

import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.jena.riot.RiotException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.RedisConnectionFailureException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import edu.tamu.iiif.exception.NotFoundException;

@RestController
@ControllerAdvice
public class GlobalExceptionHandler {

    private final static Logger LOG = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(IOException.class)
    @ResponseStatus(value = SERVICE_UNAVAILABLE)
    public @ResponseBody ResponseEntity<String> handleIOException(IOException exception) {
        ResponseEntity<String> response;
        if (StringUtils.containsIgnoreCase(ExceptionUtils.getRootCauseMessage(exception), "Broken pipe")) {
            LOG.debug("Client has disconnected before completing request.");
            response = null;
        } else {
            LOG.debug("Exception completing request. " + exception.getMessage());
            response = new ResponseEntity<String>(exception.getMessage(), SERVICE_UNAVAILABLE);
        }
        return response;
    }

    @ExceptionHandler(RiotException.class)
    @ResponseStatus(value = SERVICE_UNAVAILABLE)
    public @ResponseBody ResponseEntity<String> handleRiotException(RiotException exception) {
        LOG.debug("Exception requesting RDF. " + exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(RedisConnectionFailureException.class)
    @ResponseStatus(value = SERVICE_UNAVAILABLE)
    public @ResponseBody ResponseEntity<String> handleRedisConnectionFailureException(RedisConnectionFailureException exception) {
        LOG.debug("Exception requesting RDF. " + exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), SERVICE_UNAVAILABLE);
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = NOT_FOUND)
    public @ResponseBody ResponseEntity<String> handleNotFoundException(NotFoundException exception) {
        LOG.debug("Exception requesting RDF. " + exception.getMessage());
        return new ResponseEntity<String>(exception.getMessage(), NOT_FOUND);
    }

}
