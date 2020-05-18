package edu.tamu.iiif.config.model;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.config.model.AdminConfig.Credentials;
import edu.tamu.iiif.config.model.ResolverConfig.ResolverType;

@RunWith(SpringRunner.class)
public class ResolverConfigTest {

    @Test
    public void testResolverConfig() {
        ResolverConfig resolverConfig = new ResolverConfig();

        resolverConfig.setType(ResolverType.REMOTE);
        resolverConfig.setUrl("http://localhost:9001/entity");

        Credentials credentials = new Credentials();
        credentials.setUsername("admin");
        credentials.setPassword("admin");

        resolverConfig.setCredentials(credentials);

        assertEquals(ResolverType.REMOTE, resolverConfig.getType());
        assertEquals("http://localhost:9001/entity", resolverConfig.getUrl());
        assertEquals("admin", resolverConfig.getCredentials().getUsername());
        assertEquals("admin", resolverConfig.getCredentials().getPassword());
    }

}
