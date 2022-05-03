package edu.tamu.iiif.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RedisResourceTest {

    @Test
    public void testCreate() {
        String url = "http://localhost:900/fcrepo/rest/image02";
        RedisResource redisResource = new RedisResource(url);
        assertNotNull(redisResource);
        assertEquals(UUID.nameUUIDFromBytes(redisResource.getUrl().getBytes()).toString(), redisResource.getId());
        assertEquals(url, redisResource.getUrl());
    }

}
