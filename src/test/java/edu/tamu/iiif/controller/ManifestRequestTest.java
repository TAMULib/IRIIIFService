package edu.tamu.iiif.controller;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ManifestRequestTest {

    @Test
    public void testManifestRequest() {
        ManifestRequest request = ManifestRequest.of("test", false);
        Assert.assertEquals("test", request.getContext());
        Assert.assertEquals(false, request.isUpdate());
    }

    @Test
    public void testManifestRequestAdvanced() {
        ManifestRequest request = ManifestRequest.of("test", false, Arrays.asList(new String[] { "allow" }), Arrays.asList(new String[] { "disallow" }));
        Assert.assertEquals("test", request.getContext());
        Assert.assertEquals(false, request.isUpdate());
        Assert.assertEquals("allow", request.getAllowed());
        Assert.assertEquals("disallow", request.getDisallowed());
    }

}
