package hub.notification.service;

import hub.notification.dto.notification.NotificationRequest;
import hub.notification.dto.notification.NotificationResponse;
import hub.notification.dto.notification.RecipientDTO;
import hub.notification.model.Notification;
import hub.notification.model.NotificationRecipient;
import hub.notification.model.Recipient;
import hub.notification.repository.NotificationRecipientRepository;
import hub.notification.repository.NotificationRepository;
import hub.notification.repository.RecipientRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final RecipientRepository recipientRepository;
    private final NotificationRecipientRepository notificationRecipientRepository;

    public NotificationService(
            NotificationRepository notificationRepository,
            RecipientRepository recipientRepository,
            NotificationRecipientRepository notificationRecipientRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.recipientRepository = recipientRepository;
        this.notificationRecipientRepository = notificationRecipientRepository;
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

        return new NotificationResponse(
                notification.getId(),
                notification.getMessage(),
                notification.getCreatedAt(),
                recipientDTOs
        );
    }

}
