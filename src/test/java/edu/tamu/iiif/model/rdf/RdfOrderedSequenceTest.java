package edu.tamu.iiif.model.rdf;

import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static edu.tamu.iiif.constants.Constants.IANA_LAST_PREDICATE;

import java.io.File;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.assertj.core.util.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.utility.RdfModelUtility;

@RunWith(SpringRunner.class)
public class RdfOrderedSequenceTest {

    @Test
    public void testRdfOrderedSequence() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/fedora/rdf/pcdm_item_container.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        System.out.println(model);
        Resource resource = model.getResource("http://localhost:9000/fcrepo/rest/cars_pcdm_objects/chevy");
        RdfResource rdfResource = new RdfResource(model, resource);
        Optional<String> firstId = RdfModelUtility.getIdByPredicate(rdfResource.getModel(), IANA_FIRST_PREDICATE);
        Optional<String> lastId = RdfModelUtility.getIdByPredicate(rdfResource.getModel(), IANA_LAST_PREDICATE);

        System.out.println(firstId.get());
        System.out.println(lastId.get());

        RdfOrderedResource rdfOrderedSequence = new RdfOrderedResource(model, resource, firstId.get(), lastId.get());
        Assert.assertNotNull(rdfOrderedSequence);
        Assert.assertEquals(model, rdfOrderedSequence.getModel());

        Assert.assertEquals(firstId.get(), rdfOrderedSequence.getFirstId());
        Assert.assertEquals(lastId.get(), rdfOrderedSequence.getLastId());

        Assert.assertTrue(rdfOrderedSequence.isFirst());
        Assert.assertFalse(rdfOrderedSequence.isLast());

        rdfOrderedSequence.setCurrentId(rdfOrderedSequence.getLastId());

        Assert.assertTrue(rdfOrderedSequence.isLast());
        Assert.assertFalse(rdfOrderedSequence.isFirst());

        Assert.assertEquals(lastId.get(), rdfOrderedSequence.getCurrentId());
    }

}
