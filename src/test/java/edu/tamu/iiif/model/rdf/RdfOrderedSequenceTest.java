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

import edu.tamu.iiif.constants.Constants;
import edu.tamu.iiif.utility.RdfModelUtility;

@RunWith(SpringRunner.class)
public class RdfOrderedSequenceTest {

    @Test
    public void testCreateDefault() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/fedora/rdf/pcdm_collection_container.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource(Constants.FEDORA_HAS_PARENT_PREDICATE);
        RdfResource rdfResource = new RdfResource(model, resource);
        Optional<String> firstId = RdfModelUtility.getIdByPredicate(rdfResource.getModel(), IANA_FIRST_PREDICATE);
        Optional<String> lastId = RdfModelUtility.getIdByPredicate(rdfResource.getModel(), IANA_LAST_PREDICATE);

        RdfOrderedSequence rdfOrderedSequence = new RdfOrderedSequence(model, resource, firstId.get(), lastId.get());
        Assert.assertNotNull(rdfOrderedSequence);
        Assert.assertEquals(model, rdfOrderedSequence.getModel());
    }

}
