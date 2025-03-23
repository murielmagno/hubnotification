package hub.notification.service;

import hub.notification.dto.enums.ChannelEnum;
import hub.notification.dto.enums.ScheduleStatusEnum;
import hub.notification.dto.notification.RecipientDTO;
import hub.notification.exceptions.NotificationException;
import hub.notification.model.NotificationSchedule;
import hub.notification.repository.NotificationRecipientRepository;
import hub.notification.repository.NotificationScheduleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationSchedulerService {

    private final NotificationScheduleRepository scheduleRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    private final ExecutorService executor = Executors.newFixedThreadPool(4);

    @Scheduled(cron = "0 */1 * * * *")
    @Transactional
    public void processPendingSchedules() {
        log.info(" < --- > Inicio do processo de envio das notificações");

        List<NotificationSchedule> pendingSchedules = scheduleRepository
                .findNotificationScheduleByActiveIsTrueAndStatus(ScheduleStatusEnum.SCHEDULED);

        if (pendingSchedules.isEmpty()) {
            log.info(" < --- > Nenhum agendamento não encontrado");
            return;
        }
        Map<ChannelEnum, List<NotificationSchedule>> groupedByChannel = new EnumMap<>(ChannelEnum.class);
        for (NotificationSchedule schedule : pendingSchedules) {
            Long notificationId = schedule.getNotification().getId();
            List<RecipientDTO> recipients = notificationRecipientRepository.findRecipientDTOsByNotificationId(notificationId);
            if (!recipients.isEmpty()) {
                for (RecipientDTO recipient : recipients) {
                    ChannelEnum channel = recipient.channel();
                    groupedByChannel.computeIfAbsent(channel, k -> new ArrayList<>()).add(schedule);
                }
            }
        }
        groupedByChannel.forEach((channel, schedules) -> executor.submit(() -> {
            log.info("Enviando {} notificações via {}", schedules.size(), channel);
            for (NotificationSchedule schedule : schedules) {
                try {
                    simulateSend(schedule);
                    schedule.setStatus(ScheduleStatusEnum.SENT);
                } catch (NotificationException e) {
                    log.error("Erro ao enviar notificação {} via {}: {}", schedule.getId(), channel, e.getMessage());
                    schedule.setStatus(ScheduleStatusEnum.FAILED);
                }
                schedule.setUpdatedAt(LocalDateTime.now());
                scheduleRepository.save(schedule);
            }
        }));
    }

    private void simulateSend(NotificationSchedule schedule) {
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        if (schedule.getId() % 5 == 0) {
            throw new NotificationException("Erro simulado no envio");
        }
        log.info("Notificação {} enviada com sucesso.", schedule.getId());
    }
}
