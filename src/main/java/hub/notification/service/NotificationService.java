package hub.notification.service;

import hub.notification.dto.enums.ScheduleStatusEnum;
import hub.notification.dto.notification.NotificationRequest;
import hub.notification.dto.notification.NotificationResponse;
import hub.notification.dto.notification.NotificationScheduleResponse;
import hub.notification.dto.notification.RecipientDTO;
import hub.notification.model.Notification;
import hub.notification.model.NotificationRecipient;
import hub.notification.model.NotificationSchedule;
import hub.notification.model.Recipient;
import hub.notification.repository.NotificationRecipientRepository;
import hub.notification.repository.NotificationRepository;
import hub.notification.repository.NotificationScheduleRepository;
import hub.notification.repository.RecipientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RecipientRepository recipientRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;
    private final NotificationScheduleRepository notificationScheduleRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            RecipientRepository recipientRepository,
            NotificationRecipientRepository notificationRecipientRepository, NotificationScheduleRepository notificationScheduleRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.recipientRepository = recipientRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
        this.notificationScheduleRepository = notificationScheduleRepository;
    }

    @Transactional
    public NotificationResponse createNotification(NotificationRequest request) {
        Notification notification = Notification.builder()
                .message(request.message())
                .build();
        notification = notificationRepository.save(notification);
        List<NotificationRecipient> recipientLinks = new ArrayList<>();
        List<RecipientDTO> recipientDTOs = new ArrayList<>();

        for (RecipientDTO dto : request.recipients()) {
            String uniqueKey = dto.channel() + ":" + dto.identifier();
            Recipient recipient = recipientRepository.findByUniqueKey(uniqueKey)
                    .orElseGet(() -> {
                        Recipient newRecipient = Recipient.builder()
                                .channel(dto.channel())
                                .identifier(dto.identifier())
                                .uniqueKey(uniqueKey)
                                .build();
                        return recipientRepository.save(newRecipient);
                    });
            NotificationRecipient link = NotificationRecipient.builder()
                    .notificationId(notification.getId())
                    .recipientId(recipient.getId())
                    .build();
            recipientLinks.add(link);
            recipientDTOs.add(new RecipientDTO(recipient.getChannel(), recipient.getIdentifier()));
        }
        notificationRecipientRepository.saveAll(recipientLinks);
        notificationScheduleRepository.save(NotificationSchedule.builder().notification(notification)
                .status(ScheduleStatusEnum.SCHEDULED).build());

        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getCreatedAt(),
                recipientDTOs
        );
    }

    @Transactional
    public NotificationScheduleResponse findScheduleByNotification(Long notificationId) {
        NotificationSchedule schedule = notificationScheduleRepository.findByNotificationIdAndActiveTrue(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Agendamento não encontrado para essa notificação"));

        Notification notification = notificationRepository.findNotificationByIdAndActiveIsTrue(notificationId)
                .orElseThrow(() -> new NoSuchElementException("Notificação não encontrada"));

        List<RecipientDTO> recipients = notificationRecipientRepository.findRecipientDTOsByNotificationId(notification.getId());


        NotificationResponse notificationResponse = new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getCreatedAt(),
                recipients
        );

        return new NotificationScheduleResponse(
                schedule.getId(),
                notificationResponse,
                schedule.getStatus(),
                schedule.getCreatedAt(),
                schedule.getUpdatedAt()
        );
    }

    @Transactional
    public NotificationScheduleResponse deleteNotification(Long id) {
        Notification notification = notificationRepository.findNotificationByIdAndActiveIsTrue(id)
                .orElseThrow(() -> new NoSuchElementException("Notificação não encontrada"));
        notification.setActive(false);
        notificationRepository.save(notification);
        NotificationScheduleResponse response = null;

        var scheduleOpt = notificationScheduleRepository.findByNotificationIdAndActiveTrue(id);
        if (scheduleOpt.isPresent()) {
            var schedule = scheduleOpt.get();
            schedule.setStatus(ScheduleStatusEnum.CANCELLED);
            schedule.setActive(false);
            notificationScheduleRepository.save(schedule);
            var recipients = notificationRecipientRepository.findRecipientDTOsByNotificationId(id);
            var notificationResponse = new NotificationResponse(
                    notification.getId(),
                    notification.getMessage(),
                    notification.getCreatedAt(),
                    recipients
            );
            response = new NotificationScheduleResponse(
                    schedule.getId(),
                    notificationResponse,
                    schedule.getStatus(),
                    schedule.getCreatedAt(),
                    schedule.getUpdatedAt()
            );
        }
        return response;
    }
}
