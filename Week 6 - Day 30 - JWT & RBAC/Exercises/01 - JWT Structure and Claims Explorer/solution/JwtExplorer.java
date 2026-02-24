package com.jwt.explorer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * Exercise 01 – JWT Structure and Claims Explorer  (SOLUTION)
 */
public class JwtExplorer {

    private static final String SAMPLE_JWT =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJzdWIiOiJhbGljZSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMzYwMDB9" +
            ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    public static void main(String[] args) {
        System.out.println("=== JWT Parts ===");

        // Split on "." – gives [header, payload, signature]
        String[] parts = SAMPLE_JWT.split("\\.");

        System.out.println("Header (raw):    " + parts[0]);
        System.out.println("Payload (raw):   " + parts[1]);
        // Signature is a cryptographic HMAC-SHA256 hash encoded as Base64URL.
        // Decoding it produces raw bytes, not readable text.
        System.out.println("Signature (raw): " + parts[2]);

        System.out.println();
        System.out.println("=== Decoded Header ===");
        String header = base64UrlDecode(parts[0]);
        System.out.println(prettyPrint(header));

        System.out.println();
        System.out.println("=== Decoded Payload ===");
        String payload = base64UrlDecode(parts[1]);
        System.out.println(prettyPrint(payload));

        System.out.println();
        System.out.println("=== Claims ===");
        // Extract named claims from the payload JSON
        String sub  = extractField(payload, "sub");
        String role = extractField(payload, "role");
        long   iat  = Long.parseLong(extractField(payload, "iat"));
        long   exp  = Long.parseLong(extractField(payload, "exp"));

        System.out.println("Subject  : " + sub);
        System.out.println("Role     : " + role);
        System.out.println("Issued At: " + Instant.ofEpochSecond(iat));  // converts epoch seconds → ISO-8601
        System.out.println("Expires  : " + Instant.ofEpochSecond(exp));

        System.out.println();
        compareMethods();
    }

    static void compareMethods() {
        System.out.println("=== Token-Based vs Session-Based ===");
        // Formatted table – padded for alignment
        String fmt = "%-20s | %-26s | %s%n";
        System.out.printf(fmt, "Aspect", "Token-Based (JWT)", "Session-Based");
        System.out.println("-".repeat(20) + "-+-" + "-".repeat(26) + "-+-" + "-".repeat(29));
        System.out.printf(fmt, "Storage",        "Client (header/localStorage)", "Server (session store)");
        System.out.printf(fmt, "Scalability",    "Stateless – scales easily",   "Requires shared session store");
        System.out.printf(fmt, "Revocation",     "Hard – wait for expiry",      "Easy – delete server session");
        System.out.printf(fmt, "Server memory",  "None",                        "Session object per user");
        System.out.printf(fmt, "Cross-domain",   "Straightforward",             "Requires cookie config");
    }

    /** Decode a Base64URL-encoded string to a UTF-8 String (adds padding if needed). */
    static String base64UrlDecode(String encoded) {
        int padding = (4 - encoded.length() % 4) % 4;
        String padded = encoded + "=".repeat(padding);
        byte[] bytes = Base64.getUrlDecoder().decode(padded);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /** Extract a field value from a flat JSON string (handles strings and numbers). */
    static String extractField(String json, String fieldName) {
        String key = "\"" + fieldName + "\":";
        int start = json.indexOf(key);
        if (start == -1) return "not found";
        start += key.length();
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (json.charAt(start) == '"') {
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        } else {
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
            return json.substring(start, end);
        }
    }

    /**
     * Very simple JSON pretty-printer – inserts newlines and indentation
     * for flat, one-level JSON objects.  Not a general-purpose parser.
     */
    static String prettyPrint(String json) {
        // Replace commas with comma + newline + indent
        return json.replace("{", "{\n  ")
                   .replace(",", ",\n  ")
                   .replace("}", "\n}");
    }
}
