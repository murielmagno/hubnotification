package hub.notification.dto.notification;

import hub.notification.dto.enums.ChannelEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecipientDTO(
        @NotNull(message = "Canal é obrigatório")
        ChannelEnum channel,

        @NotBlank(message = "Identificador é obrigatório")
        String identifier
) {
}

