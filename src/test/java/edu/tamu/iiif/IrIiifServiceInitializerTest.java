package edu.tamu.iiif;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import edu.tamu.iiif.config.model.AdminConfig;
import edu.tamu.iiif.constants.Constants;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;

@SpringBootTest(classes = IrIiifServiceInitializer.class, webEnvironment = WebEnvironment.RANDOM_PORT)
public final class IrIiifServiceInitializerTest {

    @Autowired
    private AdminConfig adminConfig;

    @Test
    public void testContext() {
        assertNotNull(new Constants());
        assertEquals("admin", adminConfig.getAdmins().get(0).getUsername());
        assertEquals("abc123", adminConfig.getAdmins().get(0).getPassword());
    }

}
