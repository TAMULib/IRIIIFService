package edu.tamu.iiif.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RedisManifestTest {

    protected static final String FEDORA_PCDM_IDENTIFIER = "fedora-pcdm";

    @Test
    public void testCreateDefault() {
        RedisManifest redisManifest = new RedisManifest();
        Assert.assertNotNull(redisManifest);
        Assert.assertNotNull(redisManifest.getCreation());
        Assert.assertEquals("", redisManifest.getAllowed());
        Assert.assertEquals("", redisManifest.getDisallowed());
    }

    @Test
    public void testCreate() {
        RedisManifest redisManifest = new RedisManifest("path", ManifestType.COLLECTION, FEDORA_PCDM_IDENTIFIER, "{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}");
        Assert.assertNotNull(redisManifest);
        Assert.assertNotNull(redisManifest.getCreation());
        Assert.assertEquals("path", redisManifest.getPath());
        Assert.assertEquals(ManifestType.COLLECTION, redisManifest.getType());
        Assert.assertEquals(FEDORA_PCDM_IDENTIFIER, redisManifest.getRepository());
        Assert.assertEquals("{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}", redisManifest.getJson());
        Assert.assertEquals("", redisManifest.getAllowed());
        Assert.assertEquals("", redisManifest.getDisallowed());
    }

    @Test
    public void testCreateComplete() {
        RedisManifest redisManifest = new RedisManifest("path", ManifestType.COLLECTION, FEDORA_PCDM_IDENTIFIER, "apple,banana", "cat,dog", "{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}");
        Assert.assertNotNull(redisManifest);
        Assert.assertNotNull(redisManifest.getCreation());
        Assert.assertEquals("path", redisManifest.getPath());
        Assert.assertEquals(ManifestType.COLLECTION, redisManifest.getType());
        Assert.assertEquals(FEDORA_PCDM_IDENTIFIER, redisManifest.getRepository());
        Assert.assertEquals("{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}", redisManifest.getJson());
        Assert.assertEquals("apple,banana", redisManifest.getAllowed());
        Assert.assertEquals("cat,dog", redisManifest.getDisallowed());
    }

    @Test
    public void testUpdate() {
        RedisManifest redisManifest = new RedisManifest("path", ManifestType.COLLECTION, FEDORA_PCDM_IDENTIFIER, "apple,banana", "cat,dog", "{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}");
        redisManifest.setId("1");
        redisManifest.setJson("{\"@id\":\"http:localhost/dspace/presentation?context=123456789/123456\"}");
        Assert.assertEquals("1", redisManifest.getId());
        Assert.assertEquals("{\"@id\":\"http:localhost/dspace/presentation?context=123456789/123456\"}", redisManifest.getJson());
        Assert.assertEquals("apple,banana", redisManifest.getAllowed());
        Assert.assertEquals("cat,dog", redisManifest.getDisallowed());
    }

}
