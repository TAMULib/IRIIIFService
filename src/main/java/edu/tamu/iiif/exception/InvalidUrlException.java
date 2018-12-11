package edu.tamu.iiif.exception;

import java.io.IOException;

public class InvalidUrlException extends IOException {

    private static final long serialVersionUID = -6351161650912426116L;

    public InvalidUrlException(String message) {
        super(message);
    }

}
