package com.jwt.explorer;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;

/**
 * Exercise 01 – JWT Structure and Claims Explorer
 *
 * A JWT has the form:  header.payload.signature
 * Each part is Base64URL-encoded.  The header and payload contain JSON.
 * The signature is a cryptographic hash – it is NOT human-readable JSON.
 *
 * Run this class with: javac JwtExplorer.java && java com.jwt.explorer.JwtExplorer
 * (No extra dependencies required – uses only the Java standard library.)
 */
public class JwtExplorer {

    // Sample JWT – do NOT modify this string.
    private static final String SAMPLE_JWT =
            "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9" +
            ".eyJzdWIiOiJhbGljZSIsInJvbGUiOiJVU0VSIiwiaWF0IjoxNzAwMDAwMDAwLCJleHAiOjE3MDAwMzYwMDB9" +
            ".SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

    public static void main(String[] args) {
        System.out.println("=== JWT Parts ===");

        // TODO: Split SAMPLE_JWT on "." to get an array of 3 parts.
        //       Print each raw part labelled "Header (raw):", "Payload (raw):", "Signature (raw):".

        System.out.println();
        System.out.println("=== Decoded Header ===");

        // TODO: Base64URL-decode parts[0] and print the JSON string.
        //       Use Base64.getUrlDecoder().decode(parts[0]) then new String(bytes, StandardCharsets.UTF_8).

        System.out.println();
        System.out.println("=== Decoded Payload ===");

        // TODO: Base64URL-decode parts[1] and print the JSON string.

        System.out.println();
        System.out.println("=== Claims ===");

        // TODO: From the decoded payload JSON, extract and print:
        //       - sub  → label "Subject  :"
        //       - role → label "Role     :"
        //       - iat  → convert to Instant.ofEpochSecond(...) and label "Issued At:"
        //       - exp  → convert to Instant.ofEpochSecond(...) and label "Expires  :"
        //       Hint: simple approach – use String.split on the JSON lines or a small helper method.

        System.out.println();
        compareMethods();
    }

    /**
     * TODO: Implement this method.
     * Print a comparison table with at least 3 rows showing differences between
     * token-based (JWT) authentication and session-based authentication.
     * Suggested aspects: Storage, Scalability, Revocation, Server memory, Cross-domain.
     */
    static void compareMethods() {
        System.out.println("=== Token-Based vs Session-Based ===");
        // TODO: Print a formatted comparison table.
    }

    /**
     * Helper: decode a Base64URL string to a UTF-8 String.
     * Base64URL uses '-' and '_' instead of '+' and '/'.
     * Java's getUrlDecoder() handles this automatically.
     *
     * This method is already implemented – use it in your TODOs above.
     */
    static String base64UrlDecode(String encoded) {
        // Add padding if required (Base64 strings must be a multiple of 4 chars)
        int padding = (4 - encoded.length() % 4) % 4;
        String padded = encoded + "=".repeat(padding);
        byte[] bytes = Base64.getUrlDecoder().decode(padded);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    /**
     * Helper: extract the value of a JSON field by name from a simple flat JSON string.
     * Works for string and number values. Not a full JSON parser – only for this exercise.
     *
     * Example: extractField("{\"sub\":\"alice\",\"iat\":1700000000}", "sub") → "alice"
     *
     * This method is already implemented – use it in your TODOs above.
     */
    static String extractField(String json, String fieldName) {
        String key = "\"" + fieldName + "\":";
        int start = json.indexOf(key);
        if (start == -1) return "not found";
        start += key.length();
        // skip optional whitespace
        while (start < json.length() && json.charAt(start) == ' ') start++;
        if (json.charAt(start) == '"') {
            // string value
            int end = json.indexOf('"', start + 1);
            return json.substring(start + 1, end);
        } else {
            // numeric value
            int end = start;
            while (end < json.length() && (Character.isDigit(json.charAt(end)) || json.charAt(end) == '-')) end++;
            return json.substring(start, end);
        }
    }
}
