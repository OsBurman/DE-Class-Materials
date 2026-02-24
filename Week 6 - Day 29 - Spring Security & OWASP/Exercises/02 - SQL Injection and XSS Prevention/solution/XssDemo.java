package com.security;

/**
 * Solution – Exercise 02 XSS Prevention
 */
public class XssDemo {

    public static class HtmlEncoder {
        public static String encode(String input) {
            if (input == null) return "";
            return input
                    .replace("&", "&amp;")   // replace & first to avoid double-encoding
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
        }
    }

    /** Encodes username before embedding in HTML — prevents reflected XSS */
    public static String renderWelcomePage(String username) {
        String safe = HtmlEncoder.encode(username);
        return "<h1>Welcome, " + safe + "!</h1>";
    }

    /** Encodes comment text — prevents stored XSS */
    public static String renderComment(String commentText) {
        String safe = HtmlEncoder.encode(commentText);
        return "<p>" + safe + "</p>";
    }

    public static void main(String[] args) {
        System.out.println("\n=== XSS Demo ===");
        System.out.println("renderWelcomePage(\"Alice\")     → " + renderWelcomePage("Alice"));
        System.out.println("renderWelcomePage(\"<script>\") → " + renderWelcomePage("<script>"));
        System.out.println("renderComment(\"Nice post!\")   → " + renderComment("Nice post!"));
        System.out.println("renderComment(\"<b>bold</b>\")  → " + renderComment("<b>bold</b>"));
    }
}
