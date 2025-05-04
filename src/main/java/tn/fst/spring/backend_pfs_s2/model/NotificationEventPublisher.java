package tn.fst.spring.backend_pfs_s2.model;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventPublisher {
    private final ApplicationEventPublisher eventPublisher;

    public NotificationEventPublisher(ApplicationEventPublisher eventPublisher) {
        this.eventPublisher = eventPublisher;
    }

    public void publishNotificationCreated(Notification notification) {
        eventPublisher.publishEvent(new NotificationCreatedEvent(this, notification));
    }
}
