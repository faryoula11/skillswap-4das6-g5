
package tn.esprit.exchangeservice.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "training_sessions")
public class TrainingSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long exchangeId;

    @Column(nullable = false)
    private LocalDateTime scheduledAt;

    @Column(nullable = false)
    private Integer durationMinutes;

    @Column(length = 500)
    private String meetingLink;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SessionStatus status = SessionStatus.SCHEDULED;

    @Column(nullable = false)
    private boolean requesterConfirmed = false;

    @Column(nullable = false)
    private boolean receiverConfirmed = false;

    private LocalDateTime completedAt;

    public TrainingSession() {
    }

    public TrainingSession(
            Long exchangeId,
            LocalDateTime scheduledAt,
            Integer durationMinutes,
            String meetingLink) {

        this.exchangeId = exchangeId;
        this.scheduledAt = scheduledAt;
        this.durationMinutes = durationMinutes;
        this.meetingLink = meetingLink;
    }

    public Long getId() {
        return id;
    }

    public Long getExchangeId() {
        return exchangeId;
    }

    public LocalDateTime getScheduledAt() {
        return scheduledAt;
    }

    public Integer getDurationMinutes() {
        return durationMinutes;
    }

    public String getMeetingLink() {
        return meetingLink;
    }

    public boolean isRequesterConfirmed() {
        return requesterConfirmed;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public boolean isReceiverConfirmed() {
        return receiverConfirmed;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }


    public void assignMeetingLink(String meetingLink) {
        if (meetingLink == null || meetingLink.isBlank()) {
            throw new IllegalArgumentException(
                    "Le lien de visioconférence est obligatoire"
            );
        }

        if (this.meetingLink != null) {
            throw new IllegalStateException(
                    "Une salle de formation existe déjà"
            );
        }

        this.meetingLink = meetingLink;
    }


    public void startSession() {

        if (status != SessionStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Seule une séance programmée peut démarrer"
            );
        }

        if (meetingLink == null || meetingLink.isBlank()) {
            throw new IllegalStateException(
                    "Une salle de visioconférence est nécessaire"
            );
        }

        if (LocalDateTime.now().isBefore(scheduledAt)) {
            throw new IllegalStateException(
                    "La séance ne peut pas démarrer avant l'heure prévue"
            );
        }

        status = SessionStatus.IN_PROGRESS;
    }


    public void reschedule(
            LocalDateTime newScheduledAt,
            Integer newDurationMinutes) {

        if (status != SessionStatus.SCHEDULED) {
            throw new IllegalStateException(
                    "Seule une séance programmée peut être reprogrammée"
            );
        }

        if (newScheduledAt == null ||
                !newScheduledAt.isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException(
                    "La nouvelle date doit être dans le futur"
            );
        }

        if (newDurationMinutes == null || newDurationMinutes <= 0) {
            throw new IllegalArgumentException(
                    "La durée doit être supérieure à zéro"
            );
        }

        this.scheduledAt = newScheduledAt;
        this.durationMinutes = newDurationMinutes;
    }


    public void finishSession() {

        if (status != SessionStatus.IN_PROGRESS) {
            throw new IllegalStateException(
                    "Seule une séance en cours peut être terminée"
            );
        }

        status = SessionStatus.AWAITING_CONFIRMATION;
    }


    public void confirmParticipation(Long userId,
                                     Long requesterId,
                                     Long receiverId) {

        if (status != SessionStatus.AWAITING_CONFIRMATION) {
            throw new IllegalStateException(
                    "La séance n'attend pas de confirmation"
            );
        }

        if (userId == null) {
            throw new IllegalArgumentException(
                    "L'identifiant utilisateur est obligatoire"
            );
        }

        if (userId.equals(requesterId)) {
            if (requesterConfirmed) {
                throw new IllegalStateException(
                        "L'apprenant a déjà confirmé"
                );
            }
            requesterConfirmed = true;

        } else if (userId.equals(receiverId)) {
            if (receiverConfirmed) {
                throw new IllegalStateException(
                        "Le formateur a déjà confirmé"
                );
            }
            receiverConfirmed = true;

        } else {
            throw new SecurityException(
                    "Cet utilisateur ne participe pas à la formation"
            );
        }
        if (requesterConfirmed && receiverConfirmed) {
            status = SessionStatus.COMPLETED;
            completedAt = LocalDateTime.now();
        }
    }

}
