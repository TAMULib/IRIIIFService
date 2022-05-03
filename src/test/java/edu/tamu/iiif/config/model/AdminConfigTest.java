package edu.tamu.iiif.config.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.iiif.config.model.AdminConfig.Credentials;

@ExtendWith(SpringExtension.class)
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
