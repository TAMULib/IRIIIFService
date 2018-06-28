package edu.tamu.iiif.model;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class ManifestTypeTest {

    @Test
    public void testManifestType() {
        Assert.assertEquals(CANVAS_IDENTIFIER, ManifestType.CANVAS.getName());
        Assert.assertEquals(COLLECTION_IDENTIFIER, ManifestType.COLLECTION.getName());
        Assert.assertEquals(IMAGE_IDENTIFIER, ManifestType.IMAGE.getName());
        Assert.assertEquals(PRESENTATION_IDENTIFIER, ManifestType.PRESENTATION.getName());
        Assert.assertEquals(SEQUENCE_IDENTIFIER, ManifestType.SEQUENCE.getName());
    }

}
