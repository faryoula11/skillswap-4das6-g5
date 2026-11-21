
package tn.esprit.exchangeservice.dto;

import tn.esprit.exchangeservice.Entities.SessionStatus;
import tn.esprit.exchangeservice.Entities.TrainingSession;

import java.time.LocalDateTime;

public class TrainingSessionResponse {

    private Long id;
    private Long exchangeId;
    private LocalDateTime scheduledAt;
    private Integer durationMinutes;
    private String meetingLink;
    private SessionStatus status;
    private boolean requesterConfirmed;
    private boolean receiverConfirmed;
    private LocalDateTime completedAt;

    public TrainingSessionResponse(TrainingSession session) {
        this.id = session.getId();
        this.exchangeId = session.getExchangeId();
        this.scheduledAt = session.getScheduledAt();
        this.durationMinutes = session.getDurationMinutes();
        this.meetingLink = session.getMeetingLink();
        this.status = session.getStatus();
        this.requesterConfirmed = session.isRequesterConfirmed();
        this.receiverConfirmed = session.isReceiverConfirmed();
        this.completedAt = session.getCompletedAt();
    }

    public Long getId() { return id; }
    public Long getExchangeId() { return exchangeId; }
    public LocalDateTime getScheduledAt() { return scheduledAt; }
    public Integer getDurationMinutes() { return durationMinutes; }
    public String getMeetingLink() { return meetingLink; }
    public SessionStatus getStatus() { return status; }
    public boolean isRequesterConfirmed() { return requesterConfirmed; }
    public boolean isReceiverConfirmed() { return receiverConfirmed; }
    public LocalDateTime getCompletedAt() { return completedAt; }
}
