
package tn.esprit.exchangeservice.dto;

import tn.esprit.exchangeservice.Entities.ExchangeStatus;
import java.time.LocalDateTime;

public class ExchangeResponse {

    private Long id;
    private Long requesterId;
    private Long receiverId;
    private Long skillId;
    private ExchangeStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;

    private String requesterKeycloakId;
    private String receiverKeycloakId;

    public ExchangeResponse(
            Long id,
            Long requesterId,
            Long receiverId,
            Long skillId,
            ExchangeStatus status,
            LocalDateTime createdAt,
            LocalDateTime completedAt,
            String requesterKeycloakId,
            String receiverKeycloakId) {

        this.id = id;
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.skillId = skillId;
        this.status = status;
        this.createdAt = createdAt;
        this.completedAt = completedAt;
        this.requesterKeycloakId = requesterKeycloakId;
        this.receiverKeycloakId = receiverKeycloakId;
    }

    public String getRequesterKeycloakId() {
        return requesterKeycloakId;
    }

    public String getReceiverKeycloakId() {
        return receiverKeycloakId;
    }
    public Long getId() {
        return id;
    }

    public Long getRequesterId() {
        return requesterId;
    }

    public Long getReceiverId() {
        return receiverId;
    }

    public Long getSkillId() {
        return skillId;
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
}
