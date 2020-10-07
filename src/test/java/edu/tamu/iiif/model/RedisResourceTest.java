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
        RedisResource redisResource = new RedisResource(url);
        Assert.assertNotNull(redisResource);
        Assert.assertEquals(UUID.nameUUIDFromBytes(redisResource.getUrl().getBytes()).toString(), redisResource.getId());
        Assert.assertEquals(url, redisResource.getUrl());
    }

}
