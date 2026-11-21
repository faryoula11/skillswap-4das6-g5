package tn.esprit.exchangeservice.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.esprit.exchangeservice.Entities.Exchange;

import java.util.List;

public interface ExchangeRepository extends JpaRepository<Exchange, Long> {

    List<Exchange> findByRequesterIdOrReceiverId(
            Long requesterId,
            Long receiverId
    );


    List<Exchange> findByRequesterKeycloakIdOrReceiverKeycloakId(
            String requesterKeycloakId,
            String receiverKeycloakId
    );

}