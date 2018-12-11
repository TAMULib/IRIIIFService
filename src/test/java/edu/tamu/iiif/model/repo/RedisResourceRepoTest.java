package edu.tamu.iiif.model.repo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Optional;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.config.EmbededRedisTestConfiguration;
import edu.tamu.iiif.exception.InvalidUrlException;
import edu.tamu.iiif.model.RedisResource;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = { EmbededRedisTestConfiguration.class })
public class RedisResourceRepoTest {

    @Spy
    private JedisConnectionFactory jedisConnectionFactory;

    @Autowired
    private RedisResourceRepo redisResourceRepo;

    @Test
    public void testGetOrCreate() throws InvalidUrlException {
        RedisResource redisResource = redisResourceRepo.getOrCreate("http://localhost:9000/fcrepo/rest/image01");
        String id = redisResource.getId();
        assertNotNull(id);
        assertEquals("http://localhost:9000/fcrepo/rest/image01", redisResource.getUrl());
        redisResource = redisResourceRepo.findOne(id);
        assertNotNull(redisResource);
        assertEquals("http://localhost:9000/fcrepo/rest/image01", redisResource.getUrl());
        Optional<RedisResource> optionalRedisResource = redisResourceRepo.findByUrl("http://localhost:9000/fcrepo/rest/image01");
        assertTrue(optionalRedisResource.isPresent());
        redisResource = optionalRedisResource.get();
        assertNotNull(redisResource);
        assertEquals(id, redisResource.getId());
        redisResourceRepo.delete(redisResource);
        assertFalse(redisResourceRepo.findByUrl("http://localhost:9000/fcrepo/rest/image01").isPresent());
    }

    @Test(expected = InvalidUrlException.class)
    public void testGetOrCreateWithInvalidUrl() throws InvalidUrlException {
        redisResourceRepo.getOrCreate("fubar");
    }

}
