package edu.tamu.iiif.model.rdf;

import org.apache.jena.rdf.model.Model;

public abstract class RdfModel {

    private final Model model;

    public RdfModel(Model model) {
        this.model = model;
    }

    protected Model getModel() {
        return model;
    }

}
