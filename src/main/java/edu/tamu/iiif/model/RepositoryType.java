package edu.tamu.iiif.model;

import static edu.tamu.iiif.constants.Constants.DSPACE_IDENTIFIER;
import static edu.tamu.iiif.constants.Constants.FEDORA_IDENTIFIER;

public enum RepositoryType {

    FEDORA(FEDORA_IDENTIFIER), DSPACE(DSPACE_IDENTIFIER);

    private String name;

    RepositoryType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
