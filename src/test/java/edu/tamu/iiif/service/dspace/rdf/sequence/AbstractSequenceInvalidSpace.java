package edu.tamu.iiif.service.dspace.rdf.sequence;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import edu.tamu.iiif.controller.ManifestRequest;
import java.io.IOException;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.Test;

public abstract class AbstractSequenceInvalidSpace extends AbstractSequence {

    @Test
    public void testGetManifest() throws IOException {
        setupMocks();

        RiotException exception = assertThrows(RiotException.class, () -> {
            ManifestRequest manifestRequest = ManifestRequest.of(getManifestItemPath(), false);

            getManifestService().getManifest(manifestRequest);
        });

        assertEquals(SIMULATE_FAILURE, exception.getMessage());
    }

}
