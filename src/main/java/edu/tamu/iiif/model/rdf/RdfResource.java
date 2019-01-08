package edu.tamu.iiif.model.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

public class RdfResource {

    private final Model model;

    private Resource resource;

    public RdfResource(Model model) {
        this.model = model;
    }

    public RdfResource(Model model, String id) {
        this(model);
        this.resource = getModel().getResource(id);
    }

    public RdfResource(Model model, Resource resource) {
        this(model);
        this.resource = resource;
    }

    public RdfResource(RdfResource rdfResource, String id) {
        this(rdfResource.getModel());
        this.resource = model.getResource(id);
    }

    public RdfResource(RdfResource rdfResource, Resource resource) {
        this(rdfResource.getModel(), resource);
    }

    public Model getModel() {
        return model;
    }

    public String getId() {
        return this.resource.getURI();
    }

    public Resource getResource() {
        return resource;
    }

    public void setResource(Resource resource) {
        this.resource = resource;
    }

    public Resource getResourceById(String id) {
        return model.getResource(id);
    }

    public Property getProperty(String id) {
        return model.getProperty(id);
    }

    public Statement getStatementOfPropertyWithId(String id) {
        return resource.getProperty(getProperty(id));
    }

    public StmtIterator getStatementsOfPropertyWithId(String id) {
        return resource.listProperties(getProperty(id));
    }

    public NodeIterator getAllNodesOfPropertyWithId(String id) {
        return model.listObjectsOfProperty(getProperty(id));
    }

    public NodeIterator getNodesOfPropertyWithId(String id) {
        return model.listObjectsOfProperty(resource, getProperty(id));
    }

    public ResIterator listResourcesWithPropertyWithId(String id) {
        return model.listResourcesWithProperty(getProperty(id));
    }

    public boolean containsStatement(String propertyId, String value) {
        StmtIterator stmtItr = getStatementsOfPropertyWithId(propertyId);
        while (stmtItr.hasNext()) {
            Statement stmnt = stmtItr.next();
            if (stmnt.getResource().toString().equals(value)) {
                return true;
            }
        }
        return false;
    }

}
