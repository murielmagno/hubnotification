package hub.notification.controller.v1;

import com.fasterxml.jackson.databind.ObjectMapper;
import hub.notification.config.MockSecurityConfig;
import hub.notification.dto.enums.ChannelEnum;
import hub.notification.dto.enums.ScheduleStatusEnum;
import hub.notification.dto.notification.NotificationRequest;
import hub.notification.dto.notification.NotificationResponse;
import hub.notification.dto.notification.NotificationScheduleResponse;
import hub.notification.dto.notification.RecipientDTO;
import hub.notification.service.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = NotificationController.class)
@Import(MockSecurityConfig.class)
@AutoConfigureMockMvc
class NotificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private NotificationService service;

    @BeforeEach
    void init() {
        Mockito.reset(service);
    }

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

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldGetNotificationSchedule() throws Exception {
        Long id = 1L;
        RecipientDTO recipient = new RecipientDTO(ChannelEnum.EMAIL, "teste@teste.com");
        NotificationResponse notification = new NotificationResponse(
                id, "Mensagem", LocalDateTime.now(), List.of(recipient)
        );
        NotificationScheduleResponse response = new NotificationScheduleResponse(
                id,
                notification,
                ScheduleStatusEnum.SCHEDULED,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
        Mockito.when(service.findScheduleByNotification(anyLong())).thenReturn(response);

        mockMvc.perform(get("/v1/notifications/{id}", id))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notification.message").value("Mensagem"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldDeleteNotification() throws Exception {
        Long id = 1L;
        RecipientDTO recipient = new RecipientDTO(ChannelEnum.EMAIL, "teste@teste.com");
        NotificationResponse notification = new NotificationResponse(id, "Mensagem", LocalDateTime.now(), List.of(recipient));
        NotificationScheduleResponse response = new NotificationScheduleResponse(id, notification, ScheduleStatusEnum.SCHEDULED, LocalDateTime.now(), LocalDateTime.now());

        Mockito.when(service.deleteNotification(anyLong())).thenReturn(response);

        mockMvc.perform(delete("/v1/notifications/{id}", id))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.notification.message").value("Mensagem"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn404DeleteNotExistentNotification() throws Exception {
        Long id = 999L;
        Mockito.when(service.deleteNotification(id))
                .thenThrow(new NoSuchElementException("NOT_FOUND"));
        mockMvc.perform(delete("/v1/notifications/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn404GetNotExistentNotification() throws Exception {
        Long id = 999L;
        Mockito.when(service.findScheduleByNotification(id))
                .thenThrow(new NoSuchElementException("NOT_FOUND"));
        mockMvc.perform(get("/v1/notifications/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("NOT_FOUND"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn500ForGenericException() throws Exception {
        Mockito.when(service.findScheduleByNotification(Mockito.anyLong())).thenThrow(new RuntimeException("Erro interno"));
        mockMvc.perform(get("/v1/notifications/1"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error").value("Erro interno"));
    }

    @Test
    @WithMockUser(username = "admin", roles = "ADMIN")
    void shouldReturn400ForTypeMismatch() throws Exception {
        mockMvc.perform(get("/v1/notifications/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Parâmetro")));
    }
}
