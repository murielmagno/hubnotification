package hub.notification.repository;

import hub.notification.model.Recipient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecipientRepository extends JpaRepository<Recipient, Long> {

    Optional<Recipient> findByUniqueKey(String uniqueKey);
}
