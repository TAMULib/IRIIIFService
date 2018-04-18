package edu.tamu.iiif.model;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;

public enum ManifestType {

    COLLECTION(COLLECECTION_IDENTIFIER), PRESENTATION(PRESENTATION_IDENTIFIER), SEQUENCE(SEQUENCE_IDENTIFIER), CANVAS(CANVAS_IDENTIFIER), IMAGE(IMAGE_IDENTIFIER);

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
