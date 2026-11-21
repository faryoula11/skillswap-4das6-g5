
package tn.esprit.exchangeservice.Services;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tn.esprit.exchangeservice.Entities.Exchange;
import tn.esprit.exchangeservice.Entities.ExchangeStatus;
import tn.esprit.exchangeservice.Entities.SessionStatus;
import tn.esprit.exchangeservice.Entities.TrainingSession;
import tn.esprit.exchangeservice.Repositories.ExchangeRepository;
import tn.esprit.exchangeservice.Repositories.TrainingSessionRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.UUID;
import java.util.Arrays;

@Service
public class TrainingSessionService {

    private final TrainingSessionRepository sessionRepository;
    private final ExchangeRepository exchangeRepository;

    public TrainingSessionService(
            TrainingSessionRepository sessionRepository,
            ExchangeRepository exchangeRepository) {

        this.sessionRepository = sessionRepository;
        this.exchangeRepository = exchangeRepository;
    }

    @Transactional
    public TrainingSession scheduleSession(
            Long exchangeId,
            LocalDateTime scheduledAt,
            Integer durationMinutes) {

        if (exchangeId == null || scheduledAt == null ||
                durationMinutes == null) {
            throw new IllegalArgumentException(
                    "Tous les champs sont obligatoires"
            );
        }

        Exchange exchange = exchangeRepository
                .findById(exchangeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Échange introuvable : " + exchangeId
                ));

        if (exchange.getStatus() != ExchangeStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "La formation nécessite un échange accepté"
            );
        }

        if (!scheduledAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La date doit être dans le futur"
            );
        }

        if (durationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "La durée doit être supérieure à zéro"
            );
        }

        if (sessionRepository.existsByExchangeId(exchangeId)) {
            throw new IllegalStateException(
                    "Une séance existe déjà pour cet échange"
            );
        }

        checkSchedulingConflict(
                exchangeId,
                scheduledAt,
                durationMinutes,
                null
        );
        TrainingSession session = new TrainingSession(
                exchangeId,
                scheduledAt,
                durationMinutes,
                null
        );

        return sessionRepository.save(session);
    }

    // Consulter une séance
    @Transactional(readOnly = true)
    public TrainingSession getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException(
                        "Séance introuvable : " + id
                ));
    }

    @Transactional(readOnly = true)
    public List<TrainingSession> getSessionsByExchangeId(
            Long exchangeId) {

        return sessionRepository.findByExchangeId(exchangeId);
    }


    @Transactional
    public TrainingSession generateMeetingLink(Long sessionId) {

        TrainingSession session = getSessionById(sessionId);

        if (session.getStatus() !=
                tn.esprit.exchangeservice.Entities.SessionStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "La séance doit être programmée"
            );
        }

        String meetingLink =
                "https://meet.jit.si/SkillSwap-" + UUID.randomUUID();

        session.assignMeetingLink(meetingLink);

        return sessionRepository.save(session);
    }


    @Transactional
    public TrainingSession startSession(Long sessionId) {

        TrainingSession session = getSessionById(sessionId);

        session.startSession();

        return sessionRepository.save(session);
    }


    @Transactional
    public TrainingSession rescheduleSession(
            Long sessionId,
            LocalDateTime newScheduledAt,
            Integer newDurationMinutes) {

        TrainingSession session = getSessionById(sessionId);

        checkSchedulingConflict(
                session.getExchangeId(),
                newScheduledAt,
                newDurationMinutes,
                sessionId
        );
        session.reschedule(newScheduledAt, newDurationMinutes);

        return sessionRepository.save(session);
    }


    @Transactional
    public TrainingSession finishSession(Long sessionId) {

        TrainingSession session = getSessionById(sessionId);

        session.finishSession();

        return sessionRepository.save(session);
    }


    @Transactional
    public TrainingSession confirmSession(
            Long sessionId,
            Long userId) {

        TrainingSession session = getSessionById(sessionId);

        Exchange exchange = exchangeRepository
                .findById(session.getExchangeId())
                .orElseThrow(() -> new NoSuchElementException(
                        "Échange associé introuvable"
                ));

        session.confirmParticipation(
                userId,
                exchange.getRequesterId(),
                exchange.getReceiverId()
        );

        if (session.getStatus() == SessionStatus.COMPLETED) {
            exchange.complete();
            exchangeRepository.save(exchange);
        }
        return sessionRepository.save(session);
    }


    private void checkSchedulingConflict(
            Long exchangeId,
            LocalDateTime scheduledAt,
            Integer durationMinutes,
            Long excludedSessionId) {

        Exchange currentExchange = exchangeRepository.findById(exchangeId)
                .orElseThrow(() -> new NoSuchElementException(
                        "Échange introuvable : " + exchangeId
                ));

        LocalDateTime newEnd = scheduledAt.plusMinutes(durationMinutes);

        List<TrainingSession> existingSessions =
                sessionRepository.findByStatusIn(
                        Arrays.asList(
                                SessionStatus.SCHEDULED,
                                SessionStatus.IN_PROGRESS
                        )
                );

        for (TrainingSession existing : existingSessions) {

            if (excludedSessionId != null
                    && excludedSessionId.equals(existing.getId())) {
                continue;
            }

            Exchange otherExchange = exchangeRepository
                    .findById(existing.getExchangeId())
                    .orElse(null);

            if (otherExchange == null) {
                continue;
            }

            Long requester = currentExchange.getRequesterId();
            Long receiver = currentExchange.getReceiverId();

            boolean sameParticipant =
                    requester.equals(otherExchange.getRequesterId())
                            || requester.equals(otherExchange.getReceiverId())
                            || receiver.equals(otherExchange.getRequesterId())
                            || receiver.equals(otherExchange.getReceiverId());

            if (!sameParticipant) {
                continue;
            }

            LocalDateTime existingStart = existing.getScheduledAt();
            LocalDateTime existingEnd =
                    existingStart.plusMinutes(existing.getDurationMinutes());

            boolean overlap =
                    scheduledAt.isBefore(existingEnd)
                            && existingStart.isBefore(newEnd);

            if (overlap) {
                throw new IllegalStateException(
                        "Conflit horaire : un participant possède "
                                + "déjà une formation sur ce créneau"
                );
            }
        }
    }


}
