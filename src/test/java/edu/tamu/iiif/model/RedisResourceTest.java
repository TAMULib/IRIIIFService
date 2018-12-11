package edu.tamu.iiif.model;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RedisResourceTest {

    @Test
    public void testCreateDefault() {
        RedisResource redisResource = new RedisResource();
        Assert.assertNotNull(redisResource);
    }

    @Test
    public void testCreateWithUrl() {
        RedisResource redisResource = new RedisResource("http://localhost:900/fcrepo/rest/image01");
        Assert.assertNotNull(redisResource);
        Assert.assertEquals("http://localhost:900/fcrepo/rest/image01", redisResource.getUrl());
    }

    @Test
    public void testCreateComplete() {
        String id = UUID.randomUUID().toString();
        RedisResource redisResource = new RedisResource(id, "http://localhost:900/fcrepo/rest/image02");
        Assert.assertNotNull(redisResource);
        Assert.assertEquals(id, redisResource.getId());
        Assert.assertEquals("http://localhost:900/fcrepo/rest/image02", redisResource.getUrl());
    }

    @Test
    public void testUpdate() {
        RedisResource redisResource = new RedisResource(UUID.randomUUID().toString(), "http://localhost:900/fcrepo/rest/image02");
        String id = UUID.randomUUID().toString();
        redisResource.setId(id);
        redisResource.setUrl("http://localhost:900/fcrepo/rest/image03");
        Assert.assertNotNull(redisResource);
        Assert.assertEquals(id, redisResource.getId());
        Assert.assertEquals("http://localhost:900/fcrepo/rest/image03", redisResource.getUrl());
    }

}
