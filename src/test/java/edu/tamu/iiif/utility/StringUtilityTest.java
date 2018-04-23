package edu.tamu.iiif.utility;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class StringUtilityTest {

    @Test
    public void testCreate() {
        Assert.assertNotNull(new StringUtility());
    }

    @Test
    public void testJoinPath() {
        Assert.assertEquals("src/main/resources", StringUtility.joinPath("src", "main", "resources"));
    }

    @Test
    public void testEncode() {
        Assert.assertEquals("SGVsbG8sIFdvcmxkIQ==", StringUtility.encode("Hello, World!"));
    }

    @Test
    public void testDecode() {
        Assert.assertEquals("Hello, World!", StringUtility.decode("SGVsbG8sIFdvcmxkIQ=="));
    }

}
