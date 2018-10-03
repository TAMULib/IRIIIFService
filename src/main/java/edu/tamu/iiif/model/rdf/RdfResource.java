package edu.tamu.iiif.model.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.NodeIterator;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;

public class RdfResource extends RdfModel {

    private Resource resource;

    public RdfResource(Model model) {
        super(model);
    }

    public RdfResource(Model model, Resource resource) {
        this(model);
        this.resource = resource;
    }

    public RdfResource(RdfResource rdfResource, String id) {
        this(rdfResource.getModel());
        this.resource = getModel().getResource(id);
    }

    public RdfResource(RdfResource rdfResource, Resource resource) {
        this(rdfResource.getModel(), resource);
    }

    public Model getModel() {
        return super.getModel();
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
        return getModel().getResource(id);
    }

    public Property getProperty(String id) {
        return getModel().getProperty(id);
    }

    public Statement getStatementOfPropertyWithId(String id) {
        return resource.getProperty(getProperty(id));
    }

    public NodeIterator getAllNodesOfPropertyWithId(String id) {
        return getModel().listObjectsOfProperty(getProperty(id));
    }
    
    public NodeIterator getNodesOfPropertyWithId(String id) {
        return getModel().listObjectsOfProperty(getResource(), getProperty(id));
    }

    public ResIterator listResourcesWithPropertyWithId(String id) {
        return getModel().listResourcesWithProperty(getProperty(id));
    }

}
