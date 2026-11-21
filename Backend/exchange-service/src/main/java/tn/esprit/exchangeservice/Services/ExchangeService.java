package tn.esprit.exchangeservice.Services;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.exchangeservice.Entities.Exchange;
import tn.esprit.exchangeservice.Repositories.ExchangeRepository;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ExchangeService {

    private final ExchangeRepository exchangeRepository;

    public ExchangeService(ExchangeRepository exchangeRepository) {
        this.exchangeRepository = exchangeRepository;
    }

    @Transactional
    public Exchange createExchange(
            Long requesterId,
            Long receiverId,
            Long skillId) {

        if (requesterId == null ||
                receiverId == null ||
                skillId == null) {
            throw new IllegalArgumentException(
                    "Les identifiants sont obligatoires"
            );
        }

        if (requesterId.equals(receiverId)) {
            throw new IllegalArgumentException(
                    "Un utilisateur ne peut pas échanger avec lui-même"
            );
        }

        Exchange exchange = new Exchange();
        exchange.setRequesterId(requesterId);
        exchange.setReceiverId(receiverId);
        exchange.setSkillId(skillId);

        return exchangeRepository.save(exchange);
    }

    @Transactional(readOnly = true)
    public Exchange getExchangeById(Long id) {
        return exchangeRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Échange introuvable : " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<Exchange> getUserExchanges(Long userId) {
        return exchangeRepository
                .findByRequesterIdOrReceiverId(userId, userId);
    }

    @Transactional
    public Exchange acceptExchange(Long id, Long userId) {
        Exchange exchange = getExchangeById(id);

        if (!exchange.getReceiverId().equals(userId)) {
            throw new SecurityException(
                    "Seul le destinataire peut accepter"
            );
        }

        exchange.accept();
        return exchangeRepository.save(exchange);
    }

    @Transactional
    public Exchange rejectExchange(Long id, Long userId) {
        Exchange exchange = getExchangeById(id);

        if (!exchange.getReceiverId().equals(userId)) {
            throw new SecurityException(
                    "Seul le destinataire peut refuser"
            );
        }

        exchange.reject();
        return exchangeRepository.save(exchange);
    }

    @Transactional
    public Exchange cancelExchange(Long id, Long userId) {
        Exchange exchange = getExchangeById(id);

        if (!exchange.getRequesterId().equals(userId)) {
            throw new SecurityException(
                    "Seul le demandeur peut annuler"
            );
        }

        exchange.cancel();
        return exchangeRepository.save(exchange);
    }

    @Transactional
    public Exchange completeExchange(Long id, Long userId) {
        Exchange exchange = getExchangeById(id);

        if (!exchange.getRequesterId().equals(userId)) {
            throw new SecurityException(
                    "Seul le demandeur peut confirmer la fin"
            );
        }

        exchange.complete();
        return exchangeRepository.save(exchange);
    }


    @Transactional(readOnly = true)
    public List<Exchange> getMySecureExchanges(String keycloakId) {

        if (keycloakId == null || keycloakId.isBlank()) {
            throw new IllegalArgumentException(
                    "Identifiant Keycloak obligatoire"
            );
        }

        return exchangeRepository
                .findByRequesterKeycloakIdOrReceiverKeycloakId(
                        keycloakId,
                        keycloakId
                );
    }


    @Transactional
    public Exchange createSecureExchange(
            String requesterKeycloakId,
            String receiverKeycloakId,
            Long skillId) {

        if (requesterKeycloakId == null ||
                requesterKeycloakId.isBlank() ||
                receiverKeycloakId == null ||
                receiverKeycloakId.isBlank() ||
                skillId == null) {

            throw new IllegalArgumentException(
                    "Les identifiants des participants et la compétence sont obligatoires"
            );
        }

        if (requesterKeycloakId.equals(receiverKeycloakId)) {
            throw new IllegalArgumentException(
                    "Un utilisateur ne peut pas créer un échange avec lui-même"
            );
        }
        Exchange exchange = new Exchange();

        exchange.setSkillId(skillId);

        exchange.assignKeycloakParticipants(
                requesterKeycloakId,
                receiverKeycloakId
        );

        return exchangeRepository.save(exchange);
    }


    @Transactional
    public Exchange acceptSecureExchange(
            Long exchangeId,
            String connectedUserId) {

        Exchange exchange = getExchangeById(exchangeId);

        if (exchange.getReceiverKeycloakId() == null) {
            throw new IllegalStateException(
                    "Cet échange utilise l'ancien système d'identification"
            );
        }

        if (!exchange.getReceiverKeycloakId()
                .equals(connectedUserId)) {

            throw new SecurityException(
                    "Seul le formateur peut accepter cet échange"
            );
        }

        exchange.accept();

        return exchangeRepository.save(exchange);
    }


    @Transactional
    public Exchange rejectSecureExchange(
            Long exchangeId,
            String connectedUserId) {

        Exchange exchange = getExchangeById(exchangeId);

        if (exchange.getReceiverKeycloakId() == null) {
            throw new IllegalStateException(
                    "Cet échange utilise l'ancien système d'identification"
            );
        }

        if (!exchange.getReceiverKeycloakId().equals(connectedUserId)) {
            throw new SecurityException(
                    "Seul le formateur peut refuser cet échange"
            );
        }

        exchange.reject();

        return exchangeRepository.save(exchange);
    }


    @Transactional
    public Exchange cancelSecureExchange(
            Long exchangeId,
            String connectedUserId) {

        Exchange exchange = getExchangeById(exchangeId);

        if (exchange.getRequesterKeycloakId() == null) {
            throw new IllegalStateException(
                    "Cet échange utilise l'ancien système d'identification"
            );
        }

        if (!exchange.getRequesterKeycloakId().equals(connectedUserId)) {
            throw new SecurityException(
                    "Seul le demandeur peut annuler cet échange"
            );
        }

        exchange.cancel();

        return exchangeRepository.save(exchange);
    }


    @Transactional(readOnly = true)
    public Exchange getSecureExchangeById(
            Long exchangeId,
            String connectedUserId) {

        Exchange exchange = getExchangeById(exchangeId);

        if (exchange.getRequesterKeycloakId() == null ||
                exchange.getReceiverKeycloakId() == null) {
            throw new IllegalStateException(
                    "Cet échange utilise l'ancien système d'identification"
            );
        }

        boolean isRequester = connectedUserId.equals(
                exchange.getRequesterKeycloakId()
        );

        boolean isReceiver = connectedUserId.equals(
                exchange.getReceiverKeycloakId()
        );

        if (!isRequester && !isReceiver) {
            throw new SecurityException(
                    "Vous n'êtes pas autorisé à consulter cet échange"
            );
        }

        return exchange;
    }


}