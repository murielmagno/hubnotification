package hub.notification.model;

import hub.notification.dto.enums.ScheduleStatusEnum;
import hub.notification.model.base.BaseUpdateEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification_schedule")
public class NotificationSchedule extends BaseUpdateEntity {
    @OneToOne(optional = false)
    @JoinColumn(name = "notification_id", unique = true)
    private Notification notification;
    @Enumerated(EnumType.STRING)
    private ScheduleStatusEnum status;
}
