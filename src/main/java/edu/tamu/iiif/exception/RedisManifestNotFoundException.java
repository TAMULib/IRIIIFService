package edu.tamu.iiif.exception;

public class RedisManifestNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 187646120736905619L;

    public RedisManifestNotFoundException(String message) {
        super(message);
    }

}
