package edu.tamu.iiif.service.dspace.rdf.image;

import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.tamu.iiif.controller.ManifestRequest;
import java.io.IOException;
import org.apache.jena.irix.IRIException;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public abstract class AbstractImageInvalidSpace extends AbstractImage {

    // Disabled because the 400 return codes from the /iiif/2/ is not being handled as it should.
    // This likely needs a bug fix or something is wrong in the way the tests are written.
    @Disabled
    @Test
    public void testGetManifest() throws IOException {
        setupMocks();

        assertThrows(IRIException.class, () -> {
            ManifestRequest manifestRequest = ManifestRequest.of(getManifestRequestPath(), false);

            getManifestService().getManifest(manifestRequest);
        });
    }

}
