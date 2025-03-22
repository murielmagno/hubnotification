package hub.notification.dto.notification;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public record NotificationRequest(
        @NotBlank(message = "Mensagem é obrigatória")
        String message,

        @NotEmpty(message = "A lista de destinatários não pode estar vazia")
        List<@Valid RecipientDTO> recipients
) {
}