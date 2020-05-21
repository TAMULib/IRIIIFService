package edu.tamu.iiif.utility;

import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_DESCRIPTION;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_TITLE;
import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
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
    public void testFindIdByPredicate() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/fedora/rdf/collection_container.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Assert.assertNotNull(model);
        Optional<String> firstId = RdfModelUtility.findObject(model, IANA_FIRST_PREDICATE);
        Assert.assertTrue(firstId.isPresent());
        Assert.assertEquals("http://localhost:9000/fcrepo/rest/mwbObjects/TGWCatalog/orderProxies/ExCat0001Proxy", firstId.get());
    }

    @Test
    public void testFindObject() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource("http://localhost:8080/rdf/resource/123456789/158308");
        RdfResource rdfResource = new RdfResource(model, resource);
        Assert.assertNotNull(model);
        Optional<String> title = RdfModelUtility.findObject(rdfResource, DUBLIN_CORE_TERMS_TITLE);
        Assert.assertTrue(title.isPresent());
        Assert.assertEquals("Corvette", title.get());
    }

    @Test
    public void getObjects() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource("http://localhost:8080/rdf/resource/123456789/158308");
        RdfResource rdfResource = new RdfResource(model, resource);
        Assert.assertNotNull(model);
        List<String> values = RdfModelUtility.getObjects(rdfResource, DUBLIN_CORE_TERMS_TITLE);
        Assert.assertEquals(1, values.size());
        Assert.assertEquals("Corvette", values.get(0));
    }

    @Test
    public void getObjectsFromListOfProperties() {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource("http://localhost:8080/rdf/resource/123456789/158308");
        RdfResource rdfResource = new RdfResource(model, resource);
        Assert.assertNotNull(model);
        List<String> predicates = new ArrayList<>();
        predicates.add(DUBLIN_CORE_TERMS_TITLE);
        predicates.add(DUBLIN_CORE_TERMS_DESCRIPTION);
        List<String> values = RdfModelUtility.getObjects(rdfResource, predicates);
        Assert.assertEquals(2, values.size());
        Assert.assertEquals("Corvette", values.get(0));
        Assert.assertEquals("A fast car", values.get(1));
    }

}
