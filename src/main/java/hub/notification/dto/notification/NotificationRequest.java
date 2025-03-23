package hub.notification.dto.notification;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public record NotificationRequest(
        @Schema(description = "Mensagem da notificação", example = "Sua compra está chegando")
        @NotBlank(message = "Mensagem é obrigatória")
        String message,

        @Schema(description = "Lista de destinatários da notificação")
        @Size(min = 1)
        @NotEmpty(message = "A lista de destinatários não pode estar vazia")
        List<@Valid RecipientDTO> recipients
) {
}