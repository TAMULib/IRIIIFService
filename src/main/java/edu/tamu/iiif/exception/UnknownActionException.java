package edu.tamu.iiif.exception;

import java.io.IOException;

public class UnknownActionException extends IOException {

    private static final long serialVersionUID = 1568820536705060903L;

    public UnknownActionException(String message) {
        super(message);
    }
}
