package edu.tamu.iiif.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;

import java.util.Arrays;

import javax.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ManifestBuilderTest {

    @Test
    public void testManifestBuilder() {
        HttpServletResponse response = mock(HttpServletResponse.class);
        ManifestBuilder builder = ManifestBuilder.build(response, ManifestRequest.of("test", false, Arrays.asList(new String[] { "allow" }), Arrays.asList(new String[] { "disallow" })));
        ManifestRequest request = builder.getRequest();
        assertEquals(response, builder.getResponse());
        assertEquals("test", request.getContext());
        assertEquals(false, request.isUpdate());
        assertEquals("allow", request.getAllowed());
        assertEquals("disallow", request.getDisallowed());
    }

}
