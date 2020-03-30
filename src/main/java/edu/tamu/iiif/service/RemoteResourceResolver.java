package edu.tamu.iiif.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import edu.tamu.iiif.config.model.ResolverConfig;
import edu.tamu.iiif.exception.NotFoundException;

@Service
@ConditionalOnProperty(value = "iiif.resolver", havingValue = "remote", matchIfMissing = false)
public class RemoteResourceResolver implements ResourceResolver {

    @Autowired
    private ResolverConfig resolver;

    public String lookup(String url) throws NotFoundException {
        throw new NotFoundException(String.format("Resource with url %s not found!", url));
    }

    public String create(String url) {
        return null;
    }

    public String resolve(String id) throws NotFoundException {
        throw new NotFoundException(String.format("Resource with id %s not found!", id));
    }

    public void remove(String id) throws NotFoundException {
        throw new NotFoundException(String.format("Resource with id %s not found!", id));
    }

}
