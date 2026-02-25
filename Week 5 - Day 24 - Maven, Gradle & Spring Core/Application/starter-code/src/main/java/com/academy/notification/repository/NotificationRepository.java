package com.academy.notification.repository;

import com.academy.notification.model.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory store for sent notifications.
 *
 * Demonstrates: PROTOTYPE SCOPE
 * Each call to getBean("notificationRepository") returns a NEW instance.
 */
@Slf4j
@Repository
// TODO Task 10: Add the @Scope annotation to make this a PROTOTYPE bean
// @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
//
// After adding it, run Main.java and observe the log output:
// "Same instance? false" ← should be false (prototype)
public class NotificationRepository {

    private static final AtomicLong idSequence = new AtomicLong(1);
    private final List<Notification> store = new ArrayList<>();

    // TODO Task 11: Add @PostConstruct and @PreDestroy lifecycle methods
    // @PostConstruct — log "NotificationRepository initialized (id:
    // {this.hashCode()})"
    // @PreDestroy — log "NotificationRepository destroyed"

    /**
     * Persist a notification to the in-memory store.
     */
    public Notification save(Notification notification) {
        if (notification.getId() == null) {
            notification.setId(idSequence.getAndIncrement());
        }
        store.add(notification);
        log.info("Saved notification #{} for {}", notification.getId(), notification.getRecipient());
        return notification;
    }

    public List<Notification> findAll() {
        return new ArrayList<>(store);
    }

    public int count() {
        return store.size();
    }
}
