package edu.tamu.iiif.service;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import edu.tamu.iiif.config.model.ResolverConfig;
import edu.tamu.iiif.exception.NotFoundException;

@Service
@ConditionalOnProperty(value = "iiif.resolver", havingValue = "remote", matchIfMissing = false)
public class RemoteResourceResolver implements ResourceResolver {

    @Autowired
    private ResolverConfig resolver;

    @Autowired
    private RestTemplate restTemplate;

    public String lookup(String url) throws URISyntaxException, NotFoundException {
        URIBuilder builder = new URIBuilder(resolver.getUrl());
        builder.addParameter("url", url);
        URI uri = builder.build();
        RequestEntity<Void> request = RequestEntity.get(uri).accept(MediaType.TEXT_PLAIN).build();
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody();
        }
        throw new NotFoundException(String.format("Resource with url %s not found!", url));
    }

    public String create(String url) throws URISyntaxException {
        URIBuilder builder = new URIBuilder(resolver.getUrl());
        builder.addParameter("url", url);
        URI uri = builder.build();
        RequestEntity<String> request = RequestEntity.post(uri).accept(MediaType.TEXT_PLAIN).body(url);
        ResponseEntity<String> response = restTemplate.exchange(request, String.class);
        if (response.getStatusCode().equals(HttpStatus.CREATED)) {
            return response.getBody();
        }
        throw new RuntimeException(String.format("Failed to create resource with url %s!", url));
    }

    public String resolve(String id) throws NotFoundException {
        try {
            URIBuilder builder = new URIBuilder(StringUtils.removeEnd(resolver.getUrl(), "/") + "/" + id);
            URI uri = builder.build();
            RequestEntity<Void> request = RequestEntity.get(uri).accept(MediaType.TEXT_PLAIN).build();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            if (response.getStatusCode().equals(HttpStatus.OK)) {
                return response.getBody();
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        throw new NotFoundException(String.format("Resource with id %s not found!", id));
    }

    public void remove(String id) throws NotFoundException {
        try {
            URIBuilder builder = new URIBuilder(StringUtils.removeEnd(resolver.getUrl(), "/") + "/" + id);
            URI uri = builder.build();
            RequestEntity<Void> request = RequestEntity.delete(uri).build();
            ResponseEntity<String> response = restTemplate.exchange(request, String.class);
            if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                throw new NotFoundException(String.format("Resource with id %s not found!", id));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

}
