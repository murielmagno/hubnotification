package hub.notification.service;

import hub.notification.dto.enums.ChannelEnum;
import hub.notification.dto.notification.NotificationRequest;
import hub.notification.dto.notification.NotificationResponse;
import hub.notification.dto.notification.RecipientDTO;
import hub.notification.model.Notification;
import hub.notification.model.Recipient;
import hub.notification.repository.NotificationRecipientRepository;
import hub.notification.repository.NotificationRepository;
import hub.notification.repository.RecipientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class NotificationServiceTest {

    private AutoCloseable closeable;

    @InjectMocks
    private NotificationService service;

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private RecipientRepository recipientRepository;

    @Mock
    private NotificationRecipientRepository notificationRecipientRepository;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void shouldCreateNotificationAndRecipients() {
        RecipientDTO recipientDTO = new RecipientDTO(ChannelEnum.EMAIL, "test@example.com");
        NotificationRequest request = new NotificationRequest("Test message", List.of(recipientDTO));
        String uniqueKey = recipientDTO.channel() + ":" + recipientDTO.identifier();

        when(recipientRepository.findByUniqueKey(uniqueKey)).thenReturn(Optional.empty());
        when(recipientRepository.save(any())).thenAnswer(invocation -> {
            Recipient r = invocation.getArgument(0);
            r.setId(1L);
            return r;
        });
        when(notificationRepository.save(any())).thenAnswer(invocation -> {
            Notification n = invocation.getArgument(0);
            n.setId(1L);
            return n;
        });

        NotificationResponse notification = service.createNotification(request);

        assertNotNull(notification);
        assertEquals("Test message", notification.message());
        assertEquals(1, notification.recipients().size());

        verify(recipientRepository).save(any(Recipient.class));
        verify(notificationRepository).save(any(Notification.class));
        verify(notificationRecipientRepository).saveAll(any());
    }
}
