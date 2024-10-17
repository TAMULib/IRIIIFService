package edu.tamu.iiif.utility;

import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_DESCRIPTION;
import static edu.tamu.iiif.constants.Constants.DUBLIN_CORE_TERMS_TITLE;
import static edu.tamu.iiif.constants.Constants.IANA_FIRST_PREDICATE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.iiif.model.rdf.RdfResource;

@ExtendWith(SpringExtension.class)
public class RdfModelUtilityTest {

    @Test
    public void testCreate() {
        assertNotNull(new RdfModelUtility());
    }

    @Test
    public void testCreateRdfModel() throws IOException {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        assertNotNull(model);
    }

    @Test
    public void testFindIdByPredicate() throws IOException {
        String rdf = Files.contentOf(new File("src/test/resources/mock/fedora/rdf/collection_container.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        assertNotNull(model);
        Optional<String> firstId = RdfModelUtility.findObject(model, IANA_FIRST_PREDICATE);
        assertTrue(firstId.isPresent());
        assertEquals("http://localhost:9000/fcrepo/rest/mwbObjects/TGWCatalog/orderProxies/ExCat0001Proxy", firstId.get());
    }

    @Test
    public void testFindObject() throws IOException {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource("http://localhost:8080/rdf/resource/123456789/158308");
        RdfResource rdfResource = new RdfResource(model, resource);
        assertNotNull(model);
        Optional<String> title = RdfModelUtility.findObject(rdfResource, DUBLIN_CORE_TERMS_TITLE);
        assertTrue(title.isPresent());
        assertEquals("Corvette", title.get());
    }

    @Test
    public void getObjects() throws IOException {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource("http://localhost:8080/rdf/resource/123456789/158308");
        RdfResource rdfResource = new RdfResource(model, resource);
        assertNotNull(model);
        List<String> values = RdfModelUtility.getObjects(rdfResource, DUBLIN_CORE_TERMS_TITLE);
        assertEquals(1, values.size());
        assertEquals("Corvette", values.get(0));
    }

    @Test
    public void getObjectsFromListOfProperties() throws IOException {
        String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource("http://localhost:8080/rdf/resource/123456789/158308");
        RdfResource rdfResource = new RdfResource(model, resource);
        assertNotNull(model);
        List<String> predicates = new ArrayList<>();
        predicates.add(DUBLIN_CORE_TERMS_TITLE);
        predicates.add(DUBLIN_CORE_TERMS_DESCRIPTION);
        List<String> values = RdfModelUtility.getObjects(rdfResource, predicates);
        assertEquals(2, values.size());
        assertEquals("Corvette", values.get(0));
        assertEquals("A fast car", values.get(1));
    }

}
