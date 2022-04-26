package edu.tamu.iiif.model;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
public class ManifestTypeTest {

    @Test
    public void testManifestType() {
        assertEquals(CANVAS_IDENTIFIER, ManifestType.CANVAS.getName());
        assertEquals(COLLECTION_IDENTIFIER, ManifestType.COLLECTION.getName());
        assertEquals(IMAGE_IDENTIFIER, ManifestType.IMAGE.getName());
        assertEquals(PRESENTATION_IDENTIFIER, ManifestType.PRESENTATION.getName());
        assertEquals(SEQUENCE_IDENTIFIER, ManifestType.SEQUENCE.getName());
    }

}
