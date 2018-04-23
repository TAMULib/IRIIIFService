package edu.tamu.iiif.model;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RedisManifestTest {

    @Test
    public void testCreateDefault() {
        RedisManifest redisManifest = new RedisManifest();
        Assert.assertNotNull(redisManifest);
        Assert.assertNotNull(redisManifest.getCreation());
    }

    @Test
    public void testCreateComplete() {
        RedisManifest redisManifest = new RedisManifest("path", ManifestType.COLLECTION, RepositoryType.FEDORA, "{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}");
        Assert.assertNotNull(redisManifest);
        Assert.assertNotNull(redisManifest.getCreation());
        Assert.assertEquals("path", redisManifest.getPath());
        Assert.assertEquals(ManifestType.COLLECTION, redisManifest.getType());
        Assert.assertEquals(RepositoryType.FEDORA, redisManifest.getRepository());
        Assert.assertEquals("{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}", redisManifest.getJson());
    }

    @Test
    public void testUpdate() {
        RedisManifest redisManifest = new RedisManifest("path", ManifestType.COLLECTION, RepositoryType.FEDORA, "{\"@id\":\"http:localhost/fedora/collection?context=pcdm\"}");
        redisManifest.setId("1");
        redisManifest.setPath("new/path");
        redisManifest.setType(ManifestType.PRESENTATION);
        redisManifest.setRepository(RepositoryType.DSPACE);
        redisManifest.setJson("{\"@id\":\"http:localhost/dspace/presentation?context=123456789/123456\"}");
        Assert.assertEquals("1", redisManifest.getId());
        Assert.assertEquals("new/path", redisManifest.getPath());
        Assert.assertEquals(ManifestType.PRESENTATION, redisManifest.getType());
        Assert.assertEquals(RepositoryType.DSPACE, redisManifest.getRepository());
        Assert.assertEquals("{\"@id\":\"http:localhost/dspace/presentation?context=123456789/123456\"}", redisManifest.getJson());
    }

}
