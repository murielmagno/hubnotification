package hub.notification.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "notification_recipient")
public class NotificationRecipient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "notification_id")
    private Notification notification;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipient_id")
    private Recipient recipient;
}
