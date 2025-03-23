package hub.notification.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthRequest(
        @Schema(description = "Nome de usuário", example = "admin")
        String username,

        @Schema(description = "Senha do usuário", example = "123456")
        String password) {
}
