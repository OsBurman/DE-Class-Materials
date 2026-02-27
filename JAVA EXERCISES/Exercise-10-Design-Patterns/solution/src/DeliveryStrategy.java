/**
 * STRATEGY — DeliveryStrategy + implementations + NotificationSender (Solution)
 */
interface DeliveryStrategy {
    void deliver(Notification notification);
}

class EmailStrategy implements DeliveryStrategy {
    @Override
    public void deliver(Notification n) {
        System.out.println("Email: Sending to " + n.getRecipient() + " — Subject: " + n.getTitle());
    }
}

class SmsStrategy implements DeliveryStrategy {
    @Override
    public void deliver(Notification n) {
        System.out.println("SMS:   Sending SMS to " + n.getRecipient() + " — " + n.getBody());
    }
}

class PushStrategy implements DeliveryStrategy {
    @Override
    public void deliver(Notification n) {
        System.out.println("Push:  Push notification → device:" + n.getRecipient() + " — " + n.getTitle());
    }
}

class NotificationSender {
    private DeliveryStrategy strategy;

    public void setStrategy(DeliveryStrategy s) {
        this.strategy = s;
    }

    public void send(Notification n) {
        if (strategy == null)
            throw new IllegalStateException("No strategy set");
        strategy.deliver(n);
    }
}
