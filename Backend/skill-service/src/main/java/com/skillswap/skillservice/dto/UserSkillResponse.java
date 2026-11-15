package com.skillswap.skillservice.dto;

import com.skillswap.skillservice.model.SkillLevel;
import com.skillswap.skillservice.model.SkillType;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

@Schema(description = "Compétence déclarée par un membre")
public class UserSkillResponse {

    @Schema(description = "Identifiant unique de la déclaration", example = "10")
    private Long id;

    @Schema(description = "Identifiant du membre (sub Keycloak)", example = "user-123")
    private String userId;

    @Schema(description = "Identifiant de la compétence", example = "1")
    private Long skillId;

    @Schema(description = "Nom de la compétence", example = "Spring Boot")
    private String skillName;

    @Schema(description = "Nom de la catégorie", example = "Développement Web")
    private String categoryName;

    @Schema(description = "Type : OFFERED ou WANTED", example = "OFFERED")
    private SkillType type;

    @Schema(description = "Niveau d'expertise", example = "INTERMEDIATE")
    private SkillLevel level;

    @Schema(description = "Description personnalisée", example = "3 ans d'expérience sur Spring Boot 3")
    private String description;

    @Schema(description = "Statut actif (désactivé lors d'un soft delete)", example = "true")
    private boolean active;

    @Schema(description = "Date de création", example = "2026-11-15T22:00:00Z")
    private Instant createdAt;

    public UserSkillResponse() {
    }

    public UserSkillResponse(Long id, String userId, Long skillId, String skillName, String categoryName,
                             SkillType type, SkillLevel level, String description, boolean active, Instant createdAt) {
        this.id = id;
        this.userId = userId;
        this.skillId = skillId;
        this.skillName = skillName;
        this.categoryName = categoryName;
        this.type = type;
        this.level = level;
        this.description = description;
        this.active = active;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
    }

    public String getSkillName() {
        return skillName;
    }

    public void setSkillName(String skillName) {
        this.skillName = skillName;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public SkillType getType() {
        return type;
    }

    public void setType(SkillType type) {
        this.type = type;
    }

    public SkillLevel getLevel() {
        return level;
    }

    public void setLevel(SkillLevel level) {
        this.level = level;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
