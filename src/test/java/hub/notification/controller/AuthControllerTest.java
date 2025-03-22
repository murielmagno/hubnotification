package hub.notification.controller;

import hub.notification.dto.AuthRequest;
import hub.notification.dto.AuthResponse;
import hub.notification.service.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthService authService;
    private AuthController authController;

    @BeforeEach
    void setUp() {
        authService = mock(AuthService.class);
        authController = new AuthController(authService);
    }

    @Test
    void shouldReturnTokenInResponseEntity() {
        AuthRequest request = new AuthRequest("user", "pass");
        String expectedToken = "mocked-jwt-token";
        when(authService.authenticate(request)).thenReturn(expectedToken);

        ResponseEntity<AuthResponse> response = authController.login(request);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedToken, Objects.requireNonNull(response.getBody()).token());
        verify(authService).authenticate(request);
    }
}
