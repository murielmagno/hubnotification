package hub.notification.repository;

import hub.notification.dto.notification.RecipientDTO;
import hub.notification.model.NotificationRecipient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface NotificationRecipientRepository extends JpaRepository<NotificationRecipient, Long> {

    @Query("""
            SELECT new hub.notification.dto.notification.RecipientDTO(r.channel, r.identifier)
            FROM NotificationRecipient nr
            JOIN Recipient r ON nr.recipientId = r.id
            WHERE nr.notificationId = :notificationId
            """)
    List<RecipientDTO> findRecipientDTOsByNotificationId(Long notificationId);

}
