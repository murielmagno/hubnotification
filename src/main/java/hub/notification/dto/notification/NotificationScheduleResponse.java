package hub.notification.dto.notification;

import hub.notification.dto.enums.ScheduleStatusEnum;

import java.time.LocalDateTime;

public record NotificationScheduleResponse(
        Long id,
        NotificationResponse notification,
        ScheduleStatusEnum status,
        LocalDateTime createdAt,
        LocalDateTime updateAt
) {
}
