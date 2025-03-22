package hub.notification.model;

import hub.notification.model.base.BaseEntity;
import hub.notification.model.enums.ChannelEnum;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "recipient")
public class Recipient extends BaseEntity {
    @Enumerated(EnumType.STRING)
    private ChannelEnum channel;
    private String identifier;
    @Column(unique = true, nullable = false)
    private String uniqueKey;
}
