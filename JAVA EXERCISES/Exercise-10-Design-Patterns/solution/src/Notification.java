/** BUILDER — Notification (Solution) */
public class Notification {
    private final String title, body, recipient, priority, type;

    private Notification(Builder b) {
        this.title = b.title; this.body = b.body; this.recipient = b.recipient;
        this.priority = b.priority; this.type = b.type;
    }

    public String getTitle()     { return title; }
    public String getBody()      { return body; }
    public String getRecipient() { return recipient; }
    public String getPriority()  { return priority; }
    public String getType()      { return type; }

    @Override public String toString() {
        return "[" + type + "] '" + title + "' → " + recipient + " (" + priority + ")";
    }

    public static class Builder {
        private String title, body = "", recipient, priority = "NORMAL", type = "INFO";
        public Builder title(String v)     { title = v;     return this; }
        public Builder body(String v)      { body = v;      return this; }
        public Builder recipient(String v) { recipient = v; return this; }
        public Builder priority(String v)  { priority = v;  return this; }
        public Builder type(String v)      { type = v;      return this; }
        public Notification build() {
            if (title == null || title.isBlank())     throw new IllegalStateException("title required");
            if (recipient == null || recipient.isBlank()) throw new IllegalStateException("recipient required");
            return new Notification(this);
        }
    }
}
