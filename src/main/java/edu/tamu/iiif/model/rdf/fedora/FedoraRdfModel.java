package edu.tamu.iiif.model.rdf.fedora;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;

public abstract class FedoraRdfModel {

    private final Model model;

    public FedoraRdfModel(Model model) {
        this.model = model;
    }

    protected Model getModel() {
        return model;
    }

    protected Property getProperty(String uri) {
        return model.getProperty(uri);
    }

    protected Resource getResource(String uri) {
        return model.getResource(uri);
    }

}
