package com.skillswap.skillservice.dto;

import com.skillswap.skillservice.model.SkillLevel;
import com.skillswap.skillservice.model.SkillType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Déclaration ou modification d'une compétence par un membre")
public class UserSkillRequest {

    @NotNull(message = "L'identifiant de la compétence est obligatoire")
    @Schema(description = "Identifiant de la compétence du catalogue", example = "1")
    private Long skillId;

    @NotNull(message = "Le type de compétence est obligatoire (OFFERED ou WANTED)")
    @Schema(description = "Type : OFFERED (proposée) ou WANTED (recherchée)", example = "OFFERED")
    private SkillType type;

    @NotNull(message = "Le niveau d'expertise est obligatoire (BEGINNER, INTERMEDIATE, EXPERT)")
    @Schema(description = "Niveau d'expertise", example = "INTERMEDIATE")
    private SkillLevel level;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    @Schema(description = "Détail de votre offre ou de votre besoin", example = "3 ans d'expérience sur Spring Boot 3 et microservices")
    private String description;

    public UserSkillRequest() {
    }

    public UserSkillRequest(Long skillId, SkillType type, SkillLevel level, String description) {
        this.skillId = skillId;
        this.type = type;
        this.level = level;
        this.description = description;
    }

    public Long getSkillId() {
        return skillId;
    }

    public void setSkillId(Long skillId) {
        this.skillId = skillId;
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
}
