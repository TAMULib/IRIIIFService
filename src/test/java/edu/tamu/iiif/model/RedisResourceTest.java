package edu.tamu.iiif.model;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RedisResourceTest {

    @Test
    public void testCreate() {
        String url = "http://localhost:900/fcrepo/rest/image02";
        String id = UUID.nameUUIDFromBytes(url.getBytes()).toString();
        RedisResource redisResource = new RedisResource(id, url);
        Assert.assertNotNull(redisResource);
        Assert.assertEquals(id, redisResource.getId());
        Assert.assertEquals(url, redisResource.getUrl());
    }

}
