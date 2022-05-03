package edu.tamu.iiif.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.ReflectionTestUtils.setField;

import java.net.URISyntaxException;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.iiif.exception.NotFoundException;
import edu.tamu.iiif.model.RedisResource;
import edu.tamu.iiif.model.repo.RedisResourceRepo;

@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
public class RedisResourceResolverTest {

    @MockBean
    private RedisResourceRepo redisResourceRepo;

    private RedisResourceResolver redisResourceResolver = new RedisResourceResolver();

    private final RedisResource mockResource = new RedisResource("http://localhost:9000/fcrepo/rest/image01");

    private final RedisResource mockResourceNotExist = new RedisResource("http://localhost:9000/fcrepo/rest/image02");

    @BeforeEach
    public void setup() {
        when(redisResourceRepo.existsById(mockResource.getId())).thenReturn(true);
        when(redisResourceRepo.findById(mockResource.getId())).thenReturn(Optional.of(mockResource));
        when(redisResourceRepo.findByUrl(mockResource.getUrl())).thenReturn(Optional.of(mockResource));
        when(redisResourceRepo.save(any(RedisResource.class))).thenReturn(mockResource);

        doNothing().when(redisResourceRepo).deleteById(mockResource.getId());

        when(redisResourceRepo.findByUrl(mockResourceNotExist.getUrl())).thenReturn(Optional.empty());
        when(redisResourceRepo.existsById(mockResourceNotExist.getId())).thenReturn(false);

        setField(redisResourceResolver, "redisResourceRepo", redisResourceRepo);
    }

    @Test
    public void testLookup() throws NotFoundException, URISyntaxException {
        String id = redisResourceResolver.lookup(mockResource.getUrl());
        assertEquals(mockResource.getId(), id);
    }

    @Test
    public void testLookupNotFound() throws NotFoundException, URISyntaxException {
        Assertions.assertThrows(NotFoundException.class, () -> {
            redisResourceResolver.lookup(mockResourceNotExist.getUrl());
        });
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

    @Test
    public void testResolveNotFound() throws NotFoundException {
        Assertions.assertThrows(NotFoundException.class, () -> {
            redisResourceResolver.resolve(mockResourceNotExist.getId());
        });
    }

    @Test
    public void testRemove() throws NotFoundException {
        redisResourceResolver.remove(mockResource.getId());
    }

    @Test
    public void testRemoveNotFound() throws NotFoundException {
        Assertions.assertThrows(NotFoundException.class, () -> {
            redisResourceResolver.remove(mockResourceNotExist.getId());
        });
    }

}
