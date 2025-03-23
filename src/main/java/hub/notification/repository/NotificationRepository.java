package hub.notification.repository;

import hub.notification.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Optional<Notification> findNotificationByIdAndActiveIsTrue(Long id);
}
