package com.skillswap.skillservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Schema(description = "Requête de création ou mise à jour d'une compétence dans le catalogue")
public class SkillRequest {

    @NotBlank(message = "Le nom de la compétence est obligatoire")
    @Size(min = 2, max = 100, message = "Le nom doit contenir entre 2 et 100 caractères")
    @Schema(description = "Nom unique de la compétence", example = "Spring Boot")
    private String name;

    @Size(max = 500, message = "La description ne doit pas dépasser 500 caractères")
    @Schema(description = "Description optionnelle", example = "Framework Java pour microservices et APIs REST")
    private String description;

    @NotNull(message = "La catégorie est obligatoire")
    @Schema(description = "Identifiant de la catégorie parente", example = "1")
    private Long categoryId;

    public SkillRequest() {
    }

    public SkillRequest(String name, String description, Long categoryId) {
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
}
