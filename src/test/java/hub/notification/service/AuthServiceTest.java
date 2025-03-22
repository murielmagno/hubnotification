package hub.notification.service;

import hub.notification.component.JwtUtil;
import hub.notification.dto.auth.AuthRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthenticationManager authManager;
    private JwtUtil jwtUtil;
    private AuthService authService;

    @BeforeEach
    void setUp() {
        authManager = mock(AuthenticationManager.class);
        jwtUtil = mock(JwtUtil.class);
        authService = new AuthService(authManager, jwtUtil);
    }

    @Test
    void shouldAuthenticateAndReturnToken() {
        AuthRequest request = new AuthRequest("user", "pass");
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);
        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("user");
        when(jwtUtil.generateToken("user")).thenReturn("mocked-jwt-token");
        String token = authService.authenticate(request);
        assertEquals("mocked-jwt-token", token);
        verify(authManager).authenticate(any(UsernamePasswordAuthenticationToken.class));
        verify(jwtUtil).generateToken("user");
    }
}
