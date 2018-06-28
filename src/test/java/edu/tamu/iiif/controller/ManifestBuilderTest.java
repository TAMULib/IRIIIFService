package edu.tamu.iiif.controller;

import static org.mockito.Mockito.mock;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ManifestBuilderTest {

    @Test
    public void testManifestBuilder() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ManifestBuilder builder = ManifestBuilder.build(response, ManifestRequest.of("test", false, Arrays.asList(new String[] { "allow" }), Arrays.asList(new String[] { "disallow" })));
        ManifestRequest request = builder.getRequest();
        Assert.assertEquals(response, builder.getResponse());
        Assert.assertEquals("test", request.getContext());
        Assert.assertEquals(false, request.isUpdate());
        Assert.assertEquals("allow", request.getAllowed());
        Assert.assertEquals("disallow", request.getDisallowed());
    }

}
