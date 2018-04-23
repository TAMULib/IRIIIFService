package edu.tamu.iiif.model.rdf;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import de.digitalcollections.iiif.presentation.model.impl.v2.ImageImpl;

@RunWith(SpringRunner.class)
public class RdfCanvasTest {

    @Test
    public void testCreateDefault() {
        RdfCanvas rdfCanvas = new RdfCanvas();
        Assert.assertNotNull(rdfCanvas);
        Assert.assertNotNull(rdfCanvas.getHeight());
        Assert.assertNotNull(rdfCanvas.getWidth());
        Assert.assertNotNull(rdfCanvas.getImages());
        Assert.assertEquals(0, rdfCanvas.getImages().size());
    }

    @Test
    public void testUpdate() {
        RdfCanvas rdfCanvas = new RdfCanvas();
        rdfCanvas.setHeight(100);
        rdfCanvas.setWidth(100);
        rdfCanvas.addImage(new ImageImpl());
        Assert.assertEquals(100, rdfCanvas.getHeight());
        Assert.assertEquals(100, rdfCanvas.getWidth());
        Assert.assertEquals(1, rdfCanvas.getImages().size());
    }

}