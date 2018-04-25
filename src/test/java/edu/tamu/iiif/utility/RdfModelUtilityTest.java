package edu.tamu.iiif.utility;

import java.io.File;

import org.apache.jena.rdf.model.Model;
import org.assertj.core.util.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
public class RdfModelUtilityTest {

    @Test
    public void testCreate() {
        Assert.assertNotNull(new RdfModelUtility());
    }

    @Test
    public void testCreateRdfModel() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Assert.assertNotNull(model);
    }

}
