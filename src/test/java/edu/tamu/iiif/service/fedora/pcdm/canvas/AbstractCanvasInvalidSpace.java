package edu.tamu.iiif.service.fedora.pcdm.canvas;

import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.tamu.iiif.controller.ManifestRequest;
import java.io.IOException;
import org.apache.jena.irix.IRIException;
import org.junit.jupiter.api.Test;

public abstract class AbstractCanvasInvalidSpace extends AbstractCanvas {

    @Test
    public void testGetManifest() throws IOException {
        setupMocks();

        assertThrows(IRIException.class, () -> {
            ManifestRequest manifestRequest = ManifestRequest.of(getManifestPagePath(), false);

            getManifestService().getManifest(manifestRequest);
        });
    }

}
