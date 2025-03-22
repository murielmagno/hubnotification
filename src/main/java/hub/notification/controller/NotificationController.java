package hub.notification.controller;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@OpenAPIDefinition(
        info = @Info(title = "Notificação API", version = "v1")
)
@Tag(name = "v1", description = "API versão 1")
@RestController
@RequestMapping("/notification")
public class NotificationController {

    @GetMapping("/teste")
    public ResponseEntity<String> logout() {
        return ResponseEntity.ok("TESTE ");
    }
}
