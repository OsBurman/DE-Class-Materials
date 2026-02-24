package com.security;

/**
 * Exercise 02 – XSS Prevention
 */
public class XssDemo {

    /**
     * Simple HTML encoder — replaces dangerous characters with safe entities.
     * You must call this on any untrusted data before embedding it in HTML.
     *
     * Do NOT modify this helper — just call HtmlEncoder.encode(value).
     */
    public static class HtmlEncoder {
        public static String encode(String input) {
            if (input == null) return "";
            return input
                    .replace("&", "&amp;")   // MUST be first
                    .replace("<", "&lt;")
                    .replace(">", "&gt;")
                    .replace("\"", "&quot;")
                    .replace("'", "&#x27;");
        }
    }

    /**
     * VULNERABLE version — shown for comparison only.
     * Embeds username directly into HTML without encoding.
     */
    public static String renderWelcomePageVulnerable(String username) {
        return "<h1>Welcome, " + username + "!</h1>";
    }

    /**
     * TODO: Return "<h1>Welcome, [encoded username]!</h1>"
     *       Use HtmlEncoder.encode(username) before embedding it.
     */
    public static String renderWelcomePage(String username) {
        // TODO: String safe = HtmlEncoder.encode(username);
        // TODO: return "<h1>Welcome, " + safe + "!</h1>";
        return null;
    }

    /**
     * TODO: Return "<p>[encoded commentText]</p>"
     *       Use HtmlEncoder.encode(commentText) before embedding it.
     */
    public static String renderComment(String commentText) {
        // TODO: String safe = HtmlEncoder.encode(commentText);
        // TODO: return "<p>" + safe + "</p>";
        return null;
    }

    public static void main(String[] args) {
        System.out.println("\n=== XSS Demo ===");
        System.out.println("renderWelcomePage(\"Alice\")     → " + renderWelcomePage("Alice"));
        System.out.println("renderWelcomePage(\"<script>\") → " + renderWelcomePage("<script>"));
        System.out.println("renderComment(\"Nice post!\")   → " + renderComment("Nice post!"));
        System.out.println("renderComment(\"<b>bold</b>\")  → " + renderComment("<b>bold</b>"));
    }
}
