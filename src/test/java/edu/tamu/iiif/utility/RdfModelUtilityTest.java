package edu.tamu.iiif.utility;

import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_TITLE;
import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;

import java.io.File;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.assertj.core.util.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.model.rdf.RdfResource;

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

    @Test
    public void testGetIdByPredicate() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/fedora/rdf/pcdm_collection_container.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Assert.assertNotNull(model);
        Optional<String> firstId = RdfModelUtility.getIdByPredicate(model, IANA_FIRST_PREDICATE);
        Assert.assertTrue(firstId.isPresent());
        Assert.assertEquals("http://localhost:9000/fcrepo/rest/cars_pcdm_objects/vintage/orderProxies/page_0_proxy", firstId.get());
    }

    @Test
    public void testGetObject() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource("http://localhost:8080/rdf/resource/123456789/158308");
        RdfResource rdfResource = new RdfResource(model, resource);
        Assert.assertNotNull(model);
        Optional<String> title = RdfModelUtility.getObject(rdfResource, DUBLIN_CORE_TERMS_TITLE);
        Assert.assertTrue(title.isPresent());
        Assert.assertEquals("Corvette", title.get());
    }

}
