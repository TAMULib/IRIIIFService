package edu.tamu.iiif.config.model;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractIiifConfig {

    private List<String> labelPredicates = new ArrayList<String>();

    private List<String> descriptionPredicates = new ArrayList<String>();

    private List<String> attributionPredicates = new ArrayList<String>();

    private List<String> licensePrecedence = new ArrayList<String>();

    private List<String> metadataPrefixes = new ArrayList<String>();

    private List<String> metadataExclusion = new ArrayList<String>();

    private String url;

    private String identifier;

    private Boolean contextAsMetadata;

    public AbstractIiifConfig() {
        contextAsMetadata = true;
    }

    public List<String> getLabelPredicates() {
        return labelPredicates;
    }

    public void setLabelPredicates(List<String> labelPredicates) {
        this.labelPredicates = labelPredicates;
    }

    public List<String> getDescriptionPredicates() {
        return descriptionPredicates;
    }

    public void setDescriptionPredicates(List<String> descriptionPredicates) {
        this.descriptionPredicates = descriptionPredicates;
    }

    public List<String> getAttributionPredicates() {
        return attributionPredicates;
    }

    public void setAttributionPredicates(List<String> attributionPredicates) {
        this.attributionPredicates = attributionPredicates;
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

    public List<String> getMetadataExclusion() {
        return metadataExclusion;
    }

    public void setMetadataExclusion(List<String> metadataExclusion) {
        this.metadataExclusion = metadataExclusion;
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

    public Boolean getContextAsMetadata() {
        return contextAsMetadata;
    }

    public void setContextAsMetadata(Boolean contextAsMetadata) {
        this.contextAsMetadata = contextAsMetadata;
    }

}
