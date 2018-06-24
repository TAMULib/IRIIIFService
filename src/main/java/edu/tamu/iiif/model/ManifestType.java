package edu.tamu.iiif.model;

import static edu.tamu.iiif.constants.Constants.CANVAS_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.COLLECTION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.IMAGE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.PRESENTATION_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.SEQUENCE_IDENTIFIER;

public enum ManifestType {

    // @formatter:off
    CANVAS(CANVAS_IDENTIFIER),
    COLLECTION(COLLECTION_IDENTIFIER),
    IMAGE(IMAGE_IDENTIFIER),
    PRESENTATION(PRESENTATION_IDENTIFIER),
    SEQUENCE(SEQUENCE_IDENTIFIER);
    // @formatter:on

    private final String name;

    ManifestType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
