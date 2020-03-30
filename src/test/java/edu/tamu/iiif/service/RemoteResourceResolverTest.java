package edu.tamu.iiif.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.net.URI;
import java.net.URISyntaxException;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.utils.URIBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.client.RestTemplate;

import edu.tamu.iiif.config.model.ResolverConfig;
import edu.tamu.iiif.config.model.ResolverConfig.ResolverType;
import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;

@RunWith(SpringRunner.class)
public class RemoteResourceResolverTest {

    @MockBean
    private RestTemplate restTemplate;

    private RemoteResourceResolver remoteResourceResolver = new RemoteResourceResolver();

    private final RedisResource mockResource = new RedisResource("26f9b338-f744-11e8-8eb2-f2801f1b9fd1", "http://localhost:9000/fcrepo/rest/image01");

    private final RedisResource mockResourceNotExist = new RedisResource("26f9b338-f744-11e8-8eb2-f2801f1b9fd9", "http://localhost:9000/fcrepo/rest/image02");

    private final ResolverConfig resolver = new ResolverConfig();

    @Before
    public void setup() throws URISyntaxException {
        resolver.setType(ResolverType.REMOTE);
        resolver.setUrl("http://localhost:9001/entity");
        setField(remoteResourceResolver, "resolver", resolver);
        setField(remoteResourceResolver, "restTemplate", restTemplate);
    }

    @Test
    public void testLookup() throws NotFoundException, URISyntaxException {
        URIBuilder builder = new URIBuilder(resolver.getUrl());
        builder.addParameter("url", mockResource.getUrl());
        URI uri = builder.build();
        RequestEntity<Void> request = RequestEntity.get(uri).accept(MediaType.TEXT_PLAIN).build();

        when(restTemplate.exchange(eq(request), eq(String.class))).thenReturn(new ResponseEntity<String>(mockResource.getId(), HttpStatus.OK));

        String id = remoteResourceResolver.lookup(mockResource.getUrl());
        assertEquals(mockResource.getId(), id);
    }

    @Test(expected = NotFoundException.class)
    public void testLookupNotFound() throws NotFoundException, URISyntaxException {
        URIBuilder builder = new URIBuilder(resolver.getUrl());
        builder.addParameter("url", mockResourceNotExist.getUrl());
        URI uri = builder.build();
        RequestEntity<Void> requestNotFound = RequestEntity.get(uri).accept(MediaType.TEXT_PLAIN).build();

        when(restTemplate.exchange(eq(requestNotFound), eq(String.class))).thenReturn(new ResponseEntity<String>(String.format("Resource with url %s not found!", requestNotFound.getUrl()), HttpStatus.NOT_FOUND));

        remoteResourceResolver.lookup(mockResourceNotExist.getUrl());
    }

    @Test
    public void testCreate() throws URISyntaxException {
        URIBuilder builder = new URIBuilder(resolver.getUrl());
        builder.addParameter("url", mockResource.getUrl());
        URI uri = builder.build();
        RequestEntity<String> request = RequestEntity.post(uri).accept(MediaType.TEXT_PLAIN).body(mockResource.getUrl());

        when(restTemplate.exchange(eq(request), eq(String.class))).thenReturn(new ResponseEntity<String>(mockResource.getId(), HttpStatus.CREATED));

        String id = remoteResourceResolver.create(mockResource.getUrl());
        assertNotNull(id);
    }

    @Test
    public void testResolve() throws NotFoundException, URISyntaxException {
        URIBuilder builder = new URIBuilder(StringUtils.removeEnd(resolver.getUrl(), "/") + "/" + mockResource.getId());
        URI uri = builder.build();
        RequestEntity<Void> request = RequestEntity.get(uri).accept(MediaType.TEXT_PLAIN).build();

        when(restTemplate.exchange(eq(request), eq(String.class))).thenReturn(new ResponseEntity<String>(mockResource.getUrl(), HttpStatus.OK));

        String url = remoteResourceResolver.resolve(mockResource.getId());
        assertEquals(mockResource.getUrl(), url);
    }

    @Test(expected = NotFoundException.class)
    public void testResolveNotFound() throws NotFoundException, URISyntaxException {
        URIBuilder builder = new URIBuilder(StringUtils.removeEnd(resolver.getUrl(), "/") + "/" + mockResourceNotExist.getId());
        URI uri = builder.build();
        RequestEntity<Void> request = RequestEntity.get(uri).accept(MediaType.TEXT_PLAIN).build();

        when(restTemplate.exchange(eq(request), eq(String.class))).thenReturn(new ResponseEntity<String>(String.format("Resource with id %s not found!", mockResourceNotExist.getId()), HttpStatus.FOUND));

        remoteResourceResolver.resolve(mockResourceNotExist.getId());
    }

    @Test
    public void testRemove() throws NotFoundException, URISyntaxException {
        URIBuilder builder = new URIBuilder(StringUtils.removeEnd(resolver.getUrl(), "/") + "/" + mockResource.getId());
        URI uri = builder.build();
        RequestEntity<Void> request = RequestEntity.delete(uri).build();

        when(restTemplate.exchange(eq(request), eq(String.class))).thenReturn(new ResponseEntity<String>(HttpStatus.NO_CONTENT));

        remoteResourceResolver.remove(mockResource.getId());
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveNotFound() throws NotFoundException, URISyntaxException {
        URIBuilder builder = new URIBuilder(StringUtils.removeEnd(resolver.getUrl(), "/") + "/" + mockResourceNotExist.getId());
        URI uri = builder.build();
        RequestEntity<Void> request = RequestEntity.delete(uri).build();

        when(restTemplate.exchange(eq(request), eq(String.class))).thenReturn(new ResponseEntity<String>(String.format("Resource with id %s not found!", mockResourceNotExist.getId()), HttpStatus.NOT_FOUND));

        remoteResourceResolver.remove(mockResourceNotExist.getId());
    }

}
