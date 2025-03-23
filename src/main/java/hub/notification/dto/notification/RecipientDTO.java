package hub.notification.dto.notification;

import hub.notification.component.ValidIdentifier;
import hub.notification.dto.enums.ChannelEnum;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@ValidIdentifier
public record RecipientDTO(
        @Schema(description = "Canal de envio", example = "EMAIL, SMS, PUSH, WHATSAPP")
        @NotNull(message = "Canal é obrigatório")
        ChannelEnum channel,

        @Schema(description = "Identificador do destinatário", example = "usuario@exemplo.com")
        @NotBlank(message = "Identificador é obrigatório")
        String identifier
) {
}

