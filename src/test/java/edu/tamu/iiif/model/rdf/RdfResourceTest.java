package edu.tamu.iiif.model.rdf;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.File;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.assertj.core.util.Files;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import edu.tamu.iiif.constants.Constants;
import edu.tamu.iiif.utility.RdfModelUtility;

@ExtendWith(SpringExtension.class)
public class RdfResourceTest {

    private final static String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");

    @Test
    public void testCreateDefault() {
        Model model = RdfModelUtility.createRdfModel(rdf);
        RdfResource rdfResource = new RdfResource(model);
        assertNotNull(rdfResource);
        assertEquals(model, rdfResource.getModel());
    }

    @Test
    public void testCreateWithResource() {
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE);
        RdfResource rdfResource = new RdfResource(model, resource);
        assertNotNull(rdfResource);
        assertEquals(model, rdfResource.getModel());
        assertEquals(resource, rdfResource.getResource());
    }

    @Test
    public void testCreateFromRdfResourceWithResourceId() {
        Model testModel = RdfModelUtility.createRdfModel(rdf);
        Resource testResource = testModel.getResource(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE);
        RdfResource testRdfResource = new RdfResource(testModel, testResource);
        Resource resource = testModel.getResource(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        RdfResource rdfResource = new RdfResource(testRdfResource, Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        assertNotNull(rdfResource);
        assertEquals(resource, rdfResource.getResource());
    }

    @Test
    public void testCreateFromRdfResourceWithResourceResource() {
        Model testModel = RdfModelUtility.createRdfModel(rdf);
        Resource testResource = testModel.getResource(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE);
        RdfResource testRdfResource = new RdfResource(testModel, testResource);
        Resource resource = testModel.getResource(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        RdfResource rdfResource = new RdfResource(testRdfResource, resource);
        assertNotNull(rdfResource);
        assertEquals(resource, rdfResource.getResource());
    }

    @Test
    public void testMethods() {
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE);
        RdfResource rdfResource = new RdfResource(model, resource);

        assertEquals(model, rdfResource.getModel());
        assertEquals(resource, rdfResource.getResource());
        assertEquals(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE, rdfResource.getId());

        Resource testResource = model.getResource(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);

        rdfResource.setResource(testResource);

        assertEquals(testResource, rdfResource.getResource());
        assertEquals(Constants.DSPACE_HAS_BITSTREAM_PREDICATE, rdfResource.getId());

        assertEquals(resource, rdfResource.getResourceById(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE));

        Property property = model.getProperty(Constants.DUBLIN_CORE_TERMS_TITLE);

        assertEquals(property, rdfResource.getProperty(Constants.DUBLIN_CORE_TERMS_TITLE));

        Statement statement = resource.getProperty(property);

        assertEquals(statement, rdfResource.getStatementOfPropertyWithId(Constants.DUBLIN_CORE_TERMS_TITLE));

        NodeIterator nodeIterator = rdfResource.getAllNodesOfPropertyWithId(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        assertNotNull(nodeIterator);

        assertEquals(1, nodeIterator.toList().size());

        ResIterator resIterator = rdfResource.listResourcesWithPropertyWithId(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        assertNotNull(resIterator);

        assertEquals(1, resIterator.toList().size());
    }

}
