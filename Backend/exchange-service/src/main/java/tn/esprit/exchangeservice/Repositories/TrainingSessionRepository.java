
package tn.esprit.exchangeservice.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.exchangeservice.Entities.TrainingSession;
import tn.esprit.exchangeservice.Entities.SessionStatus;

import java.util.List;
import java.util.Optional;

public interface TrainingSessionRepository
        extends JpaRepository<TrainingSession, Long> {

    List<TrainingSession> findByExchangeId(Long exchangeId);

    List<TrainingSession> findByStatusIn(
            List<SessionStatus> statuses
    );


    boolean existsByExchangeId(Long exchangeId);

    Optional<TrainingSession> findByIdAndExchangeId(
            Long id,
            Long exchangeId
    );
}
