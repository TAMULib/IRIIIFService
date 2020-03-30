package edu.tamu.iiif.config;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.config.model.AdminConfig;
import edu.tamu.iiif.config.model.AdminConfig.Credentials;

@RunWith(SpringRunner.class)
public class AdminConfigTest {

    @Test
    public void testAdminConfig() {
        AdminConfig adminConfig = new AdminConfig();
        List<Credentials> admins = new ArrayList<Credentials>();
        Credentials credentials = new Credentials();
        credentials.setUsername("admin");
        credentials.setPassword("abc123");

        admins.add(credentials);
        adminConfig.setAdmins(admins);

        assertEquals(1, adminConfig.getAdmins().size());
        assertEquals("admin", adminConfig.getAdmins().get(0).getUsername());
        assertEquals("abc123", adminConfig.getAdmins().get(0).getPassword());
    }

}
