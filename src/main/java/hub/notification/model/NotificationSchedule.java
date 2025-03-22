package hub.notification.model;

import hub.notification.model.base.BaseUpdateEntity;
import hub.notification.model.enums.ScheduleStatusEnum;
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
    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_id")
    private Notification notification;
    @Enumerated(EnumType.STRING)
    private ScheduleStatusEnum status = ScheduleStatusEnum.SCHEDULED;
}
