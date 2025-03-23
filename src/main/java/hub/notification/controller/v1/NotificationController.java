package hub.notification.controller.v1;

import hub.notification.dto.notification.NotificationRequest;
import hub.notification.dto.notification.NotificationResponse;
import hub.notification.dto.notification.NotificationScheduleResponse;
import hub.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Notificações", description = "Gerenciamento de notificações")
@RestController
@RequestMapping("/v1/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @Operation(
            summary = "Criar nova notificação",
            description = "Cria uma nova notificação com a mensagem e a lista de destinatários."
    )
    @ApiResponse(responseCode = "200", description = "Notificação criada com sucesso")
    @ApiResponse(responseCode = "400", description = "Requisição inválida")
    @PostMapping
    public ResponseEntity<NotificationResponse> create(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Dados da notificação a ser criada",
                    required = true
            )
            @RequestBody @Valid NotificationRequest request
    ) {
        return ResponseEntity.ok(notificationService.createNotification(request));
    }

    @Operation(
            summary = "Buscar agendamento de notificação",
            description = "Retorna o agendamento associado à notificação pelo ID."
    )
    @ApiResponse(responseCode = "200", description = "Agendamento encontrado")
    @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    @GetMapping("/{id}")
    public ResponseEntity<NotificationScheduleResponse> getNotificationSchedule(
            @Parameter(description = "ID da notificação", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(notificationService.findScheduleByNotification(id));
    }

    @Operation(
            summary = "Cancelar uma notificação",
            description = "Marca a notificação como inativa e cancela o agendamento."
    )
    @ApiResponse(responseCode = "200", description = "Notificação cancelada com sucesso")
    @ApiResponse(responseCode = "404", description = "Notificação não encontrada")
    @DeleteMapping("/{id}")
    public ResponseEntity<NotificationScheduleResponse> deleteNotification(
            @Parameter(description = "ID da notificação a ser cancelada", required = true)
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(notificationService.deleteNotification(id));
    }
}

