package edu.tamu.iiif.model;

import static edu.tamu.iiif.constants.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.FEDORA_IDENTIFIER;

public enum RepositoryType {

    // @formatter:off
    DSPACE(DSPACE_IDENTIFIER),
    FEDORA(FEDORA_IDENTIFIER);
    // @formatter:on

    private final String name;

    RepositoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
