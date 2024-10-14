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
        boolean disableEncode = "true".equalsIgnoreCase(System.getenv("DEBUG_DISABLE_SPACE_ENCODE"));
        System.out.println("\n\n\nDEBUG: DEBUG_DISABLE_SPACE_ENCODE: " + disableEncode + "\n\n\n");
        if (disableEncode) {
            System.out.print("\n\n\nDEBUG: not encoding (replacing spaces), from '" + value + "'\n\n\n");
            return value;
        }

        System.out.print("\n\n\nDEBUG: encoding (replacing spaces), from '" + value + "' to '" + value.replace(" ", "%20") + "'\n\n\n");
        return value.replace(" ", "%20");
    }

}
