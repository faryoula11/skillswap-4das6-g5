package tn.esprit.exchangeservice.Entities;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "exchanges")
public class Exchange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "requester_id")
    private Long requesterId;

    @Column(name = "receiver_id")
    private Long receiverId;


    @Column(name = "requester_keycloak_id")
    private String requesterKeycloakId;

    @Column(name = "receiver_keycloak_id")
    private String receiverKeycloakId;


    @Column(nullable = false)
    private Long skillId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ExchangeStatus status = ExchangeStatus.PENDING;

    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
    }

    public void accept() {
        if (status != ExchangeStatus.PENDING) {
            throw new IllegalStateException(
                    "Seule une demande en attente peut être acceptée"
            );
        }
        status = ExchangeStatus.ACCEPTED;
    }

    public void reject() {
        if (status != ExchangeStatus.PENDING) {
            throw new IllegalStateException(
                    "Seule une demande en attente peut être refusée"
            );
        }
        status = ExchangeStatus.REJECTED;
    }

    public void cancel() {
        if (status != ExchangeStatus.PENDING) {
            throw new IllegalStateException(
                    "Seule une demande en attente peut être annulée"
            );
        }
        status = ExchangeStatus.CANCELLED;
    }

    public void complete() {
        if (status != ExchangeStatus.ACCEPTED) {
            throw new IllegalStateException(
                    "Seule une demande acceptée peut être terminée"
            );
        }
        status = ExchangeStatus.COMPLETED;
        completedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }


    public Long getRequesterId() {
        return requesterId;
    }

    public void setRequesterId(Long requesterId) {
        this.requesterId = requesterId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public ExchangeStatus getStatus() {
        return status;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }


    public String getRequesterKeycloakId() {
        return requesterKeycloakId;
    }

    public String getReceiverKeycloakId() {
        return receiverKeycloakId;
    }


    public void assignKeycloakParticipants(
            String requesterKeycloakId,
            String receiverKeycloakId) {

        if (requesterKeycloakId == null ||
                requesterKeycloakId.isBlank() ||
                receiverKeycloakId == null ||
                receiverKeycloakId.isBlank()) {

            throw new IllegalArgumentException(
                    "Les identifiants Keycloak sont obligatoires"
            );
        }

        if (requesterKeycloakId.equals(receiverKeycloakId)) {
            throw new IllegalArgumentException(
                    "Un utilisateur ne peut pas échanger avec lui-même"
            );
        }

        if (this.requesterKeycloakId != null ||
                this.receiverKeycloakId != null) {
            throw new IllegalStateException(
                    "Les participants Keycloak sont déjà définis"
            );
        }

        this.requesterKeycloakId = requesterKeycloakId;
        this.receiverKeycloakId = receiverKeycloakId;
    }

}