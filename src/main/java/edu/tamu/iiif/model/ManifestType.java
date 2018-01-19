package edu.tamu.iiif.model;

import static edu.tamu.iiif.constants.rdf.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.rdf.Constants.PRESENTATION_IDENTIFIER;

public enum ManifestType {

    COLLECTION(COLLECECTION_IDENTIFIER), PRESENTATION(PRESENTATION_IDENTIFIER), IMAGE(IMAGE_IDENTIFIER);

    private String name;

    ManifestType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
