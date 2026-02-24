package com.jwt.util;

/**
 * Quick demo main – run to see JWT generation/validation in the console.
 * Not required for the tests, but useful for manual exploration.
 */
public class JwtDemoMain {

    public static void main(String[] args) {
        JwtUtil util = new JwtUtil();

        String token = util.generateToken("alice", "ADMIN");
        System.out.println("Generated token: " + token);
        System.out.println();
        System.out.println("Username : " + util.extractUsername(token));
        System.out.println("Role     : " + util.extractRole(token));
        System.out.println("Expires  : " + util.extractExpiry(token));
        System.out.println("Valid?   : " + util.validateToken(token));

        System.out.println();
        System.out.println("=== Tampered token ===");
        // Flip the last character of the signature
        String[] parts = token.split("\\.");
        String sig = parts[2];
        char flipped = (sig.charAt(sig.length() - 1) == 'a') ? 'b' : 'a';
        String tampered = parts[0] + "." + parts[1] + "." + sig.substring(0, sig.length() - 1) + flipped;
        System.out.println("Valid?   : " + util.validateToken(tampered));
    }
}
