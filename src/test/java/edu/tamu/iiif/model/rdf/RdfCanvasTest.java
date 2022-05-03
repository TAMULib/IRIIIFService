package edu.tamu.iiif.model.rdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import de.digitalcollections.iiif.presentation.model.impl.v2.ImageImpl;
import edu.tamu.iiif.model.ImageWithInfo;

@ExtendWith(SpringExtension.class)
public class RdfCanvasTest {

    @Test
    public void testCreateDefault() {
        RdfCanvas rdfCanvas = new RdfCanvas();
        assertNotNull(rdfCanvas);
        assertNotNull(rdfCanvas.getHeight());
        assertNotNull(rdfCanvas.getWidth());
        assertNotNull(rdfCanvas.getImages());
        assertEquals(0, rdfCanvas.getImages().size());
    }

    @Test
    public void testUpdate() {
        RdfCanvas rdfCanvas = new RdfCanvas();
        rdfCanvas.setHeight(100);
        rdfCanvas.setWidth(100);
        rdfCanvas.addImage(ImageWithInfo.of(new ImageImpl()));
        assertEquals(100, rdfCanvas.getHeight());
        assertEquals(100, rdfCanvas.getWidth());
        assertEquals(1, rdfCanvas.getImages().size());
    }

}
