package com.skillswap.skillservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Détails d'une compétence du catalogue")
public class SkillResponse {

    @Schema(description = "Identifiant de la compétence", example = "1")
    private Long id;

    @Schema(description = "Nom de la compétence", example = "Spring Boot")
    private String name;

    @Schema(description = "Description de la compétence", example = "Framework Java pour microservices")
    private String description;

    @Schema(description = "Identifiant de la catégorie parente", example = "1")
    private Long categoryId;

    @Schema(description = "Nom de la catégorie parente", example = "Développement Web")
    private String categoryName;

    public SkillResponse() {
    }

    public SkillResponse(Long id, String name, String description, Long categoryId, String categoryName) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }
}
