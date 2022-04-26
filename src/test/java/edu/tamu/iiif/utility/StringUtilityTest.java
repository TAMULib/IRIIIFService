package edu.tamu.iiif.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class StringUtilityTest {

    @Test
    public void testCreate() {
        assertNotNull(new StringUtility());
    }

    @Test
    public void testJoinPath() {
        assertEquals("src/main/resources", StringUtility.joinPath("src", "main", "resources"));
    }

    @Test
    public void testEncode() {
        assertEquals("SGVsbG8sIFdvcmxkIQ==", StringUtility.encode("Hello, World!"));
    }

    @Test
    public void testDecode() {
        assertEquals("Hello, World!", StringUtility.decode("SGVsbG8sIFdvcmxkIQ=="));
    }

}
