
package tn.esprit.exchangeservice.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.exchangeservice.Entities.TrainingSession;
import tn.esprit.exchangeservice.dto.ScheduleSessionRequest;
import tn.esprit.exchangeservice.dto.TrainingSessionResponse;
import tn.esprit.exchangeservice.Services.TrainingSessionService;
import java.util.List;

@RestController
@RequestMapping("/api/training-sessions")
public class TrainingSessionController {

    private final TrainingSessionService sessionService;

    public TrainingSessionController(
            TrainingSessionService sessionService) {
        this.sessionService = sessionService;
    }

    @PostMapping
    public ResponseEntity<TrainingSessionResponse> schedule(
            @RequestBody ScheduleSessionRequest request) {

        TrainingSession session = sessionService.scheduleSession(
                request.getExchangeId(),
                request.getScheduledAt(),
                request.getDurationMinutes()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new TrainingSessionResponse(session));
    }

    @GetMapping("/{id}")
    public TrainingSessionResponse getById(@PathVariable Long id) {

        return new TrainingSessionResponse(
                sessionService.getSessionById(id)
        );
    }

    @GetMapping("/exchange/{exchangeId}")
    public List<TrainingSessionResponse> getByExchange(
            @PathVariable Long exchangeId) {

        return sessionService
                .getSessionsByExchangeId(exchangeId)
                .stream()
                .map(TrainingSessionResponse::new)
                .toList();
    }


    @PostMapping("/{id}/meeting-link")
    public TrainingSessionResponse generateMeetingLink(
            @PathVariable Long id) {

        return new TrainingSessionResponse(
                sessionService.generateMeetingLink(id)
        );
    }


    @PatchMapping("/{id}/start")
    public TrainingSessionResponse startSession(
            @PathVariable Long id) {

        TrainingSession session = sessionService.startSession(id);

        return new TrainingSessionResponse(session);
    }


    @PatchMapping("/{id}/reschedule")
    public TrainingSessionResponse reschedule(
            @PathVariable Long id,
            @RequestBody ScheduleSessionRequest request) {

        TrainingSession session = sessionService.rescheduleSession(
                id,
                request.getScheduledAt(),
                request.getDurationMinutes()
        );

        return new TrainingSessionResponse(session);
    }


    @PatchMapping("/{id}/finish")
    public TrainingSessionResponse finishSession(
            @PathVariable Long id) {

        TrainingSession session = sessionService.finishSession(id);

        return new TrainingSessionResponse(session);
    }


    @PatchMapping("/{id}/confirm")
    public TrainingSessionResponse confirmSession(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return new TrainingSessionResponse(
                sessionService.confirmSession(id, userId)
        );
    }

}
