package edu.tamu.iiif;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.config.AdminConfig;
import edu.tamu.iiif.constants.Constants;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = IrIiifServiceInitializer.class)
public class IrIiifServiceInitializerTest {

    @Autowired
    private AdminConfig adminConfig;

    @Test
    public void testContext() {
        assertNotNull(new Constants());
        assertEquals("admin", adminConfig.getAdmins().get(0).getUsername());
        assertEquals("abc123", adminConfig.getAdmins().get(0).getPassword());
    }

}
