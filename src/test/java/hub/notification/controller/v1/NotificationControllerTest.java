package hub.notification.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import hub.notification.config.MockSecurityConfig;
import hub.notification.dto.enums.ChannelEnum;
import hub.notification.dto.notification.NotificationRequest;
import hub.notification.dto.notification.NotificationResponse;
import hub.notification.dto.notification.RecipientDTO;
import hub.notification.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
@Import(MockSecurityConfig.class)
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationService service;

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldCreateNotification() throws Exception {
        RecipientDTO recipientDTO = new RecipientDTO(ChannelEnum.EMAIL, "teste@teste.com");
        NotificationRequest request = new NotificationRequest("Mensagem teste", List.of(recipientDTO));
        NotificationResponse notification = new NotificationResponse(
                1L,
                "Mensagem teste",
                LocalDateTime.now(),
                List.of(recipientDTO)
        );
        Mockito.when(service.createNotification(Mockito.any())).thenReturn(notification);

        mockMvc.perform(post("/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Mensagem teste"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn400WhenRequestIsInvalid() throws Exception {
        NotificationRequest request = new NotificationRequest(null, List.of());

        mockMvc.perform(post("/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn403WhenRequestIsNotAuthorized() throws Exception {
        RecipientDTO recipientDTO = new RecipientDTO(ChannelEnum.EMAIL, "naoimporta@email.com");
        NotificationRequest request = new NotificationRequest("Mensagem", List.of(recipientDTO));

        mockMvc.perform(post("/v1/notifications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

}

