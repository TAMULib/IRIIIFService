package edu.tamu.iiif.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;

@RunWith(SpringRunner.class)
public class RedisResourceResolverTest {

    @MockBean
    private RedisResourceRepo redisResourceRepo;

    private RedisResourceResolver redisResourceResolver = new RedisResourceResolver();

    private final RedisResource mockResource = new RedisResource("http://localhost:9000/fcrepo/rest/image01");

    private final RedisResource mockResourceNotExist = new RedisResource("http://localhost:9000/fcrepo/rest/image02");

    @Before
    public void setup() {
        when(redisResourceRepo.exists(mockResource.getId())).thenReturn(true);
        when(redisResourceRepo.findOne(mockResource.getId())).thenReturn(mockResource);
        when(redisResourceRepo.findByUrl(mockResource.getUrl())).thenReturn(Optional.of(mockResource));
        when(redisResourceRepo.save(any(RedisResource.class))).thenReturn(mockResource);

        doNothing().when(redisResourceRepo).delete(mockResource.getId());

        when(redisResourceRepo.findByUrl(mockResourceNotExist.getUrl())).thenReturn(Optional.empty());
        when(redisResourceRepo.exists(mockResourceNotExist.getId())).thenReturn(false);

        setField(redisResourceResolver, "redisResourceRepo", redisResourceRepo);
    }

    @Test
    public void testLookup() throws NotFoundException, URISyntaxException {
        String id = redisResourceResolver.lookup(mockResource.getUrl());
        assertEquals(mockResource.getId(), id);
    }

    @Test(expected = NotFoundException.class)
    public void testLookupNotFound() throws NotFoundException, URISyntaxException {
        redisResourceResolver.lookup(mockResourceNotExist.getUrl());
    }

    @Test
    public void testCreate() throws URISyntaxException {
        String id = redisResourceResolver.create(mockResource.getUrl());
        assertNotNull(id);
    }

    @Test
    public void testResolve() throws NotFoundException {
        String url = redisResourceResolver.resolve(mockResource.getId());
        assertEquals(mockResource.getUrl(), url);
    }

    @Test(expected = NotFoundException.class)
    public void testResolveNotFound() throws NotFoundException {
        redisResourceResolver.resolve(mockResourceNotExist.getId());
    }

    @Test
    public void testRemove() throws NotFoundException {
        redisResourceResolver.remove(mockResource.getId());
    }

    @Test(expected = NotFoundException.class)
    public void testRemoveNotFound() throws NotFoundException {
        redisResourceResolver.remove(mockResourceNotExist.getId());
    }

}
