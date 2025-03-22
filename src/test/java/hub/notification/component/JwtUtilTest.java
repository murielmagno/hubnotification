package hub.notification.component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private final String secretKey = "MySuperSecretKeyThatIsLongEnoughForHMAC256123456";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(jwtUtil, "secretKey", secretKey);
    }

    @Test
    void shouldGenerateValidTokenAndExtractUsername() {
        String username = "user123";
        String token = jwtUtil.generateToken(username);
        String extractedUsername = jwtUtil.extractUsername(token);
        assertEquals(username, extractedUsername);
    }

    @Test
    void shouldValidateTokenCorrectly() {
        String username = "user123";
        String token = jwtUtil.generateToken(username);
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn(username);
        boolean isValid = jwtUtil.isTokenValid(token, userDetails);
        assertTrue(isValid);
    }

    @Test
    void shouldDetectExpiredToken() {
        JwtUtil shortLivedJwtUtil = new JwtUtil();
        ReflectionTestUtils.setField(shortLivedJwtUtil, "secretKey", secretKey);
        String token = generateExpiredToken();
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("user123");
        boolean isValid = shortLivedJwtUtil.isTokenValid(token, userDetails);
        assertFalse(isValid);
    }

    public static String generateShortLivedToken(String secret, String username, int secondsToLive) {
        SecretKey key = Keys.hmacShaKeyFor(secret.getBytes());
        return Jwts.builder()
                .claim("sub", username)
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(secondsToLive, ChronoUnit.SECONDS)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

    private String generateExpiredToken() {
        SecretKey key = Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .claim("sub", "user123")
                .issuedAt(Date.from(Instant.now().minus(2, ChronoUnit.MINUTES)))
                .expiration(Date.from(Instant.now().minus(1, ChronoUnit.MINUTES)))
                .signWith(key, Jwts.SIG.HS256)
                .compact();
    }

}
