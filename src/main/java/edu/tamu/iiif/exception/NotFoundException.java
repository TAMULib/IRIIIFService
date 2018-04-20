package edu.tamu.iiif.exception;

import java.io.IOException;

public class NotFoundException extends IOException {

    private static final long serialVersionUID = 5987071043942965320L;

    public NotFoundException(String message) {
        super(message);
    }

}
