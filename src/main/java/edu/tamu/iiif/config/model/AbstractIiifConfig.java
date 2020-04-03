package edu.tamu.iiif.config.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractIiifConfig {

    private List<String> labelPrecedence = new ArrayList<String>();

    private List<String> descriptionPrecedence = new ArrayList<String>();

    private List<String> attributionPrecedence = new ArrayList<String>();

    private List<String> licensePrecedence = new ArrayList<String>();

    private List<String> metadataPrefixes = new ArrayList<String>();

    private String url;

    private String identifier;

    public List<String> getLabelPrecedence() {
        return labelPrecedence;
    }

    public void setLabelPrecedence(List<String> labelPrecedence) {
        this.labelPrecedence = labelPrecedence;
    }

    public List<String> getDescriptionPrecedence() {
        return descriptionPrecedence;
    }

    public void setDescriptionPrecedence(List<String> descriptionPrecedence) {
        this.descriptionPrecedence = descriptionPrecedence;
    }

    public List<String> getAttributionPrecedence() {
        return attributionPrecedence;
    }

    public void setAttributionPrecedence(List<String> attributionPrecedence) {
        this.attributionPrecedence = attributionPrecedence;
    }

    public List<String> getLicensePrecedence() {
        return licensePrecedence;
    }

    public void setLicensePrecedence(List<String> licensePrecedence) {
        this.licensePrecedence = licensePrecedence;
    }

    public List<String> getMetadataPrefixes() {
        return metadataPrefixes;
    }

    public void setMetadataPrefixes(List<String> metadataPrefixes) {
        this.metadataPrefixes = metadataPrefixes;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
