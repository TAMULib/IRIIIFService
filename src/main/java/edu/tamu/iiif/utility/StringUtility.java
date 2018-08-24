package edu.tamu.iiif.utility;

import java.util.Base64;

public class StringUtility {

    public static String joinPath(String... parts) {
        return String.join("/", parts);
    }

    public static String encode(String value) {
        return new String(Base64.getEncoder().encode(value.getBytes()));
    }

    public static String decode(String value) {
        return new String(Base64.getDecoder().decode(value.getBytes()));
    }

    public static String encodeSpaces(String value) {
        return value.replace(" ", "%20");
    }

}
