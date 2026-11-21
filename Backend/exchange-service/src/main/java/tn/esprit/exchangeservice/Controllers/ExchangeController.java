
package tn.esprit.exchangeservice.Controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import tn.esprit.exchangeservice.Entities.Exchange;
import tn.esprit.exchangeservice.dto.CreateExchangeRequest;
import tn.esprit.exchangeservice.dto.ExchangeResponse;
import tn.esprit.exchangeservice.Services.ExchangeService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

import tn.esprit.exchangeservice.dto.CreateSecureExchangeRequest;

import java.util.List;

@RestController
@RequestMapping("/api/exchanges")
public class ExchangeController {

    private final ExchangeService exchangeService;

    public ExchangeController(ExchangeService exchangeService) {
        this.exchangeService = exchangeService;
    }


    private ExchangeResponse toResponse(Exchange exchange) {
        return new ExchangeResponse(
                exchange.getId(),
                exchange.getRequesterId(),
                exchange.getReceiverId(),
                exchange.getSkillId(),
                exchange.getStatus(),
                exchange.getCreatedAt(),
                exchange.getCompletedAt(),
                exchange.getRequesterKeycloakId(),
                exchange.getReceiverKeycloakId()
        );
    }

    @PostMapping
    public ResponseEntity<ExchangeResponse> create(
            @RequestBody CreateSecureExchangeRequest request,
            @AuthenticationPrincipal Jwt jwt) {

        Exchange exchange = exchangeService.createSecureExchange(
                jwt.getSubject(),
                request.getReceiverKeycloakId(),
                request.getSkillId()
        );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(toResponse(exchange));
    }


    @GetMapping("/{id}")
    public ExchangeResponse getById(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        return toResponse(
                exchangeService.getSecureExchangeById(
                        id,
                        jwt.getSubject()
                )
        );
    }



    @GetMapping("/my")
    public List<ExchangeResponse> getMyExchanges(
            @AuthenticationPrincipal Jwt jwt) {

        return exchangeService
                .getMySecureExchanges(jwt.getSubject())
                .stream()
                .map(this::toResponse)
                .toList();
    }


    @PatchMapping("/{id}/accept")
    public ExchangeResponse accept(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        return toResponse(
                exchangeService.acceptSecureExchange(
                        id,
                        jwt.getSubject()
                )
        );
    }



    @PatchMapping("/{id}/reject")
    public ExchangeResponse reject(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        return toResponse(
                exchangeService.rejectSecureExchange(
                        id,
                        jwt.getSubject()
                )
        );
    }



    @PatchMapping("/{id}/cancel")
    public ExchangeResponse cancel(
            @PathVariable Long id,
            @AuthenticationPrincipal Jwt jwt) {

        return toResponse(
                exchangeService.cancelSecureExchange(
                        id,
                        jwt.getSubject()
                )
        );
    }

    @PatchMapping("/{id}/complete")
    public ExchangeResponse complete(
            @PathVariable Long id,
            @RequestParam Long userId) {

        return toResponse(exchangeService.completeExchange(id, userId));
    }
}
