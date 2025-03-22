package hub.notification.dto.notification;

import java.time.LocalDateTime;
import java.util.List;

public record NotificationResponse(
        Long id,
        String message,
        LocalDateTime createdAt,
        List<RecipientDTO> recipients
) {
}
