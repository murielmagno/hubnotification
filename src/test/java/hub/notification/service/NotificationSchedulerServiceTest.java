package hub.notification.service;

import hub.notification.dto.enums.ChannelEnum;
import hub.notification.dto.enums.ScheduleStatusEnum;
import hub.notification.dto.notification.RecipientDTO;
import hub.notification.model.Notification;
import hub.notification.model.NotificationSchedule;
import hub.notification.repository.NotificationRecipientRepository;
import hub.notification.repository.NotificationScheduleRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerServiceTest {

    @InjectMocks
    private NotificationSchedulerService schedulerService;

    @Mock
    private NotificationScheduleRepository scheduleRepository;

    @Mock
    private NotificationRecipientRepository notificationRecipientRepository;

    @Test
    void shouldProcessAndGroupPendingSchedules() {
        Notification notification = Notification.builder()
                .message("Mensagem")
                .build();
        notification.setId(1L);

        NotificationSchedule schedule = NotificationSchedule.builder()
                .notification(notification)
                .status(ScheduleStatusEnum.SCHEDULED)
                .build();

        when(scheduleRepository.findNotificationScheduleByActiveIsTrueAndStatus(ScheduleStatusEnum.SCHEDULED))
                .thenReturn(List.of(schedule));
        when(notificationRecipientRepository.findRecipientDTOsByNotificationId(1L))
                .thenReturn(List.of(new RecipientDTO(ChannelEnum.EMAIL, "user@test.com"),
                        new RecipientDTO(ChannelEnum.SMS, "11999998888")));
        schedulerService.processPendingSchedules();
        verify(scheduleRepository).findNotificationScheduleByActiveIsTrueAndStatus(ScheduleStatusEnum.SCHEDULED);
        verify(notificationRecipientRepository).findRecipientDTOsByNotificationId(1L);
    }

    @Test
    void shouldNotProcessWhenNoPendingSchedules() {
        when(scheduleRepository.findNotificationScheduleByActiveIsTrueAndStatus(ScheduleStatusEnum.SCHEDULED))
                .thenReturn(Collections.emptyList());
        schedulerService.processPendingSchedules();
        verify(scheduleRepository).findNotificationScheduleByActiveIsTrueAndStatus(ScheduleStatusEnum.SCHEDULED);
        verifyNoInteractions(notificationRecipientRepository);
    }

    @Test
    void shouldMarkScheduleAsFailedSend() {
        Notification notification = Notification.builder().message("Erro").build();
        notification.setActive(true);
        notification.setId(5L);
        NotificationSchedule schedule = NotificationSchedule.builder()
                .notification(notification)
                .status(ScheduleStatusEnum.SCHEDULED)
                .build();
        schedule.setActive(true);
        schedule.setId(5L);
        List<NotificationSchedule> schedules = List.of(schedule);
        List<RecipientDTO> recipients = List.of(new RecipientDTO(ChannelEnum.EMAIL, "test@email.com"));
        when(notificationRecipientRepository.findRecipientDTOsByNotificationId(5L))
                .thenReturn(recipients);
        when(scheduleRepository.findNotificationScheduleByActiveIsTrueAndStatus(ScheduleStatusEnum.SCHEDULED))
                .thenReturn(schedules);
        schedulerService.processPendingSchedules();
        await().untilAsserted(() ->
                verify(scheduleRepository).save(argThat(saved ->
                        saved.getStatus() == ScheduleStatusEnum.FAILED
                ))
        );
    }

}
