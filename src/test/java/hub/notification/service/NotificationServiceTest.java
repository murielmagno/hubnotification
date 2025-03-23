package hub.notification.service;

import hub.notification.dto.enums.ChannelEnum;
import hub.notification.dto.enums.ScheduleStatusEnum;
import hub.notification.dto.notification.NotificationRequest;
import hub.notification.dto.notification.NotificationResponse;
import hub.notification.dto.notification.NotificationScheduleResponse;
import hub.notification.dto.notification.RecipientDTO;
import hub.notification.model.Notification;
import hub.notification.model.NotificationSchedule;
import hub.notification.model.Recipient;
import hub.notification.repository.NotificationRecipientRepository;
import hub.notification.repository.NotificationRepository;
import hub.notification.repository.NotificationScheduleRepository;
import hub.notification.repository.RecipientRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

    @Mock
    private NotificationScheduleRepository notificationScheduleRepository;

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

    @Test
    void shouldFindNotificationSchedule() {
        Long notificationId = 1L;
        Notification notification = Notification.builder()
                .message("Agendada")
                .build();
        notification.setId(notificationId);
        NotificationSchedule schedule = NotificationSchedule.builder()
                .notification(notification)
                .build();
        schedule.setStatus(ScheduleStatusEnum.SCHEDULED);
        when(notificationScheduleRepository.findByNotificationIdAndActiveTrue(notificationId)).thenReturn(Optional.of(schedule));
        when(notificationRepository.findNotificationByIdAndActiveIsTrue(notificationId)).thenReturn(Optional.of(notification));
        when(notificationRecipientRepository.findRecipientDTOsByNotificationId(notificationId))
                .thenReturn(List.of(new RecipientDTO(ChannelEnum.EMAIL, "email@teste.com")));
        NotificationScheduleResponse response = service.findScheduleByNotification(notificationId);
        assertNotNull(response);
        assertEquals(notificationId, response.notification().id());
        assertEquals(schedule.getId(), response.id());
    }

    @Test
    void shouldDeleteNotificationWithSchedule() {
        Long notificationId = 1L;
        Notification notification = Notification.builder()
                .message("Test")
                .build();
        NotificationSchedule schedule = NotificationSchedule.builder()
                .notification(notification)
                .build();
        schedule.setStatus(ScheduleStatusEnum.SCHEDULED);
        when(notificationRepository.findNotificationByIdAndActiveIsTrue(notificationId)).thenReturn(Optional.of(notification));
        when(notificationScheduleRepository.findByNotificationIdAndActiveTrue(notificationId)).thenReturn(Optional.of(schedule));

        service.deleteNotification(notificationId);
        assertFalse(notification.isActive());
        assertFalse(schedule.isActive());
        assertEquals(ScheduleStatusEnum.CANCELLED, schedule.getStatus());
        verify(notificationRepository).save(notification);
        verify(notificationScheduleRepository).save(schedule);
    }

    @Test
    void shouldThrowExceptionWhenScheduleNotFound() {
        Long id = 123L;
        when(notificationScheduleRepository.findByNotificationIdAndActiveTrue(id))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findScheduleByNotification(id));
    }

    @Test
    void shouldThrowExceptionWhenNotificationNotFoundForDelete() {
        Long id = 123L;
        when(notificationRepository.findNotificationByIdAndActiveIsTrue(id))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.deleteNotification(id));
    }

    @Test
    void shouldReuseExistingRecipientWhenCreatingNotification() {
        RecipientDTO recipientDTO = new RecipientDTO(ChannelEnum.EMAIL, "existing@example.com");
        NotificationRequest request = new NotificationRequest("Mensagem", List.of(recipientDTO));
        String uniqueKey = recipientDTO.channel() + ":" + recipientDTO.identifier();
        Recipient existingRecipient = Recipient.builder()
                .channel(recipientDTO.channel())
                .identifier(recipientDTO.identifier())
                .uniqueKey(uniqueKey)
                .build();
        existingRecipient.setId(1L);
        when(recipientRepository.findByUniqueKey(uniqueKey)).thenReturn(Optional.of(existingRecipient));
        when(notificationRepository.save(any())).thenAnswer(i -> {
            Notification n = i.getArgument(0);
            n.setId(1L);
            return n;
        });
        NotificationResponse response = service.createNotification(request);
        assertNotNull(response);
        verify(recipientRepository, never()).save(any());
        verify(notificationRecipientRepository).saveAll(any());
    }

    @Test
    void shouldReturnNullScheduleWhenNotificationHasNoSchedule() {
        Long id = 1L;
        Notification notification = Notification.builder().message("Teste").build();
        notification.setId(id);
        when(notificationRepository.findNotificationByIdAndActiveIsTrue(id))
                .thenReturn(Optional.of(notification));
        when(notificationScheduleRepository.findByNotificationIdAndActiveTrue(id))
                .thenReturn(Optional.empty());
        NotificationScheduleResponse response = service.deleteNotification(id);
        assertNull(response);
        verify(notificationRepository).save(notification);
    }

    @Test
    void shouldThrowExceptionIfScheduleExistsButNotificationIsNotActive() {
        Long id = 1L;
        NotificationSchedule schedule = NotificationSchedule.builder().build();
        when(notificationScheduleRepository.findByNotificationIdAndActiveTrue(id))
                .thenReturn(Optional.of(schedule));
        when(notificationRepository.findNotificationByIdAndActiveIsTrue(id))
                .thenReturn(Optional.empty());
        assertThrows(NoSuchElementException.class, () -> service.findScheduleByNotification(id));
    }
}
