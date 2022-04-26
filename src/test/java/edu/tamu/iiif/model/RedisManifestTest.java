package edu.tamu.iiif.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class RedisManifestTest {

    protected static final String FEDORA_PCDM_IDENTIFIER = "fedora-pcdm";

    @Test
    public void testCreateDefault() {
        RedisManifest redisManifest = new RedisManifest();
        assertNotNull(redisManifest);
        assertNotNull(redisManifest.getCreation());
        assertEquals("", redisManifest.getAllowed());
        assertEquals("", redisManifest.getDisallowed());
    }

    @Test
    public void testCreate() {
        RedisManifest redisManifest = new RedisManifest("path", ManifestType.COLLECTION, FEDORA_PCDM_IDENTIFIER, "{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}");
        assertNotNull(redisManifest);
        assertNotNull(redisManifest.getCreation());
        assertEquals("path", redisManifest.getPath());
        assertEquals(ManifestType.COLLECTION, redisManifest.getType());
        assertEquals(FEDORA_PCDM_IDENTIFIER, redisManifest.getRepository());
        assertEquals("{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}", redisManifest.getJson());
        assertEquals("", redisManifest.getAllowed());
        assertEquals("", redisManifest.getDisallowed());
    }

    @Test
    public void testCreateComplete() {
        RedisManifest redisManifest = new RedisManifest("path", ManifestType.COLLECTION, FEDORA_PCDM_IDENTIFIER, "apple,banana", "cat,dog", "{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}");
        assertNotNull(redisManifest);
        assertNotNull(redisManifest.getCreation());
        assertEquals("path", redisManifest.getPath());
        assertEquals(ManifestType.COLLECTION, redisManifest.getType());
        assertEquals(FEDORA_PCDM_IDENTIFIER, redisManifest.getRepository());
        assertEquals("{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}", redisManifest.getJson());
        assertEquals("apple,banana", redisManifest.getAllowed());
        assertEquals("cat,dog", redisManifest.getDisallowed());
    }

    @Test
    public void testUpdate() {
        RedisManifest redisManifest = new RedisManifest("path", ManifestType.COLLECTION, FEDORA_PCDM_IDENTIFIER, "apple,banana", "cat,dog", "{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}");
        redisManifest.setId("1");
        redisManifest.setJson("{\"@id\":\"http:localhost/dspace/presentation?context=123456789/123456\"}");
        assertEquals("1", redisManifest.getId());
        assertEquals("{\"@id\":\"http:localhost/dspace/presentation?context=123456789/123456\"}", redisManifest.getJson());
        assertEquals("apple,banana", redisManifest.getAllowed());
        assertEquals("cat,dog", redisManifest.getDisallowed());
    }

}
