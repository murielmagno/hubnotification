package hub.notification.repository;

import hub.notification.dto.enums.ScheduleStatusEnum;
import hub.notification.model.NotificationSchedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface NotificationScheduleRepository extends JpaRepository<NotificationSchedule, Long> {

    @Query("SELECT ns FROM NotificationSchedule ns JOIN ns.notification n WHERE n.id = :id AND ns.active = true")
    Optional<NotificationSchedule> findByNotificationIdAndActiveTrue(Long id);

    List<NotificationSchedule> findNotificationScheduleByActiveIsTrueAndStatus(ScheduleStatusEnum status);

}
