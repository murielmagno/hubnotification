package hub.notification.controller.v1;

import hub.notification.dto.notification.NotificationRequest;
import hub.notification.dto.notification.NotificationResponse;
import hub.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OpenAPIDefinition(
        info = @Info(title = "Notificação API", version = "v1")
)
@Tag(name = "v1", description = "API versão 1")
@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    public ResponseEntity<NotificationResponse> create(@RequestBody @Valid NotificationRequest request) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }
}
