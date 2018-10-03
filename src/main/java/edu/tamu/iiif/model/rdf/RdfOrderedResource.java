package edu.tamu.iiif.model.rdf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;

public class RdfOrderedResource extends RdfResource {

    private final String firstId;

    private final String lastId;

    private String currentId;

    public RdfOrderedResource(Model model, Resource resource, String firstId, String lastId) {
        super(model, resource);
        this.firstId = firstId;
        this.lastId = lastId;
        this.currentId = firstId;
    }

    public String getFirstId() {
        return firstId;
    }

    public String getLastId() {
        return lastId;
    }

    public String getCurrentId() {
        return currentId;
    }

    public void setCurrentId(String currentId) {
        this.currentId = currentId;
    }

    public boolean isFirst() {
        return currentId.equals(firstId);
    }

    public boolean isLast() {
        return currentId.equals(lastId);
    }

}
