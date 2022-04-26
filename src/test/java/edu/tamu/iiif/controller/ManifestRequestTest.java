package edu.tamu.iiif.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ManifestRequestTest {

    @Test
    public void testManifestRequest() {
        ManifestRequest request = ManifestRequest.of("test", false);
        assertEquals("test", request.getContext());
        assertEquals(false, request.isUpdate());
    }

    @Test
    public void testManifestRequestAdvanced() {
        ManifestRequest request = ManifestRequest.of("test", false, Arrays.asList(new String[] { "allow" }), Arrays.asList(new String[] { "disallow" }));
        assertEquals("test", request.getContext());
        assertEquals(false, request.isUpdate());
        assertEquals("allow", request.getAllowed());
        assertEquals("disallow", request.getDisallowed());
    }

}
