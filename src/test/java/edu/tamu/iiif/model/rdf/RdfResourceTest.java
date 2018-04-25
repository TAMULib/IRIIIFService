package edu.tamu.iiif.model.rdf;

import java.io.File;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.assertj.core.util.Files;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.junit4.SpringRunner;

import edu.tamu.iiif.constants.Constants;
import edu.tamu.iiif.utility.RdfModelUtility;

@RunWith(SpringRunner.class)
public class RdfResourceTest {

    private final static String rdf = Files.contentOf(new File("src/test/resources/mock/dspace/rdf/item.rdf"), "UTF-8");

    @Test
    public void testCreateDefault() {
        Model model = RdfModelUtility.createRdfModel(rdf);
        RdfResource rdfResource = new RdfResource(model);
        Assert.assertNotNull(rdfResource);
        Assert.assertEquals(model, rdfResource.getModel());
    }

    @Test
    public void testCreateWithResource() {
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE);
        RdfResource rdfResource = new RdfResource(model, resource);
        Assert.assertNotNull(rdfResource);
        Assert.assertEquals(model, rdfResource.getModel());
        Assert.assertEquals(resource, rdfResource.getResource());
    }

    @Test
    public void testCreateFromRdfResourceWithResourceId() {
        Model testModel = RdfModelUtility.createRdfModel(rdf);
        Resource testResource = testModel.getResource(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE);
        RdfResource testRdfResource = new RdfResource(testModel, testResource);
        Resource resource = testModel.getResource(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        RdfResource rdfResource = new RdfResource(testRdfResource, Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        Assert.assertNotNull(rdfResource);
        Assert.assertEquals(resource, rdfResource.getResource());
    }

    @Test
    public void testCreateFromRdfResourceWithResourceResource() {
        Model testModel = RdfModelUtility.createRdfModel(rdf);
        Resource testResource = testModel.getResource(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE);
        RdfResource testRdfResource = new RdfResource(testModel, testResource);
        Resource resource = testModel.getResource(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        RdfResource rdfResource = new RdfResource(testRdfResource, resource);
        Assert.assertNotNull(rdfResource);
        Assert.assertEquals(resource, rdfResource.getResource());
    }

    @Test
    public void testMethods() {
        Model model = RdfModelUtility.createRdfModel(rdf);
        Resource resource = model.getResource(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE);
        RdfResource rdfResource = new RdfResource(model, resource);

        Assert.assertEquals(model, rdfResource.getModel());
        Assert.assertEquals(resource, rdfResource.getResource());
        Assert.assertEquals(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE, rdfResource.getId());

        Resource testResource = model.getResource(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);

        rdfResource.setResource(testResource);

        Assert.assertEquals(testResource, rdfResource.getResource());
        Assert.assertEquals(Constants.DSPACE_HAS_BITSTREAM_PREDICATE, rdfResource.getId());

        Assert.assertEquals(resource, rdfResource.getResourceById(Constants.DSPACE_IS_PART_OF_COLLECTION_PREDICATE));

        Property property = model.getProperty(Constants.DUBLIN_CORE_TERMS_TITLE);

        Assert.assertEquals(property, rdfResource.getProperty(Constants.DUBLIN_CORE_TERMS_TITLE));

        Statement statement = resource.getProperty(property);

        Assert.assertEquals(statement, rdfResource.getStatementOfPropertyWithId(Constants.DUBLIN_CORE_TERMS_TITLE));

        NodeIterator nodeIterator = rdfResource.getAllNodesOfPropertyWithId(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        Assert.assertNotNull(nodeIterator);

        Assert.assertEquals(1, nodeIterator.toList().size());

        ResIterator resIterator = rdfResource.listResourcesWithPropertyWithId(Constants.DSPACE_HAS_BITSTREAM_PREDICATE);
        Assert.assertNotNull(resIterator);

        Assert.assertEquals(1, resIterator.toList().size());
    }

}
