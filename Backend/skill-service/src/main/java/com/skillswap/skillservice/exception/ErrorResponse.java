package com.skillswap.skillservice.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;
import java.util.Map;

@Schema(description = "Format d'erreur Problem Details (RFC 7807)")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    @Schema(description = "URI de référence vers le type d'erreur", example = "about:blank")
    private String type;

    @Schema(description = "Titre court et lisible de l'erreur", example = "Ressource introuvable")
    private String title;

    @Schema(description = "Code statut HTTP", example = "404")
    private int status;

    @Schema(description = "Explication détaillée de l'erreur", example = "La compétence avec l'identifiant 42 n'existe pas")
    private String detail;

    @Schema(description = "URI de la requête ayant provoqué l'erreur", example = "/api/skills/42")
    private String instance;

    @Schema(description = "Horodatage de l'erreur", example = "2026-11-15T22:30:00Z")
    private Instant timestamp;

    @Schema(description = "Erreurs détaillées par champ lors de la validation")
    private Map<String, String> errors;

    public ErrorResponse() {
        this.timestamp = Instant.now();
        this.type = "about:blank";
    }

    public ErrorResponse(int status, String title, String detail, String instance) {
        this();
        this.status = status;
        this.title = title;
        this.detail = detail;
        this.instance = instance;
    }

    public ErrorResponse(int status, String title, String detail, String instance, Map<String, String> errors) {
        this(status, title, detail, instance);
        this.errors = errors;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}
