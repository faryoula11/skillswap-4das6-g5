package com.skillswap.skillservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Détails d'une catégorie du catalogue")
public class CategoryResponse {

    @Schema(description = "Identifiant unique de la catégorie", example = "1")
    private Long id;

    @Schema(description = "Nom de la catégorie", example = "Développement Web")
    private String name;

    @Schema(description = "Description de la catégorie", example = "Compétences liées au développement web")
    private String description;

    @Schema(description = "Nombre de compétences dans cette catégorie", example = "5")
    private long skillsCount;

    public CategoryResponse() {
    }

    public CategoryResponse(Long id, String name, String description, long skillsCount) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.skillsCount = skillsCount;
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

    public long getSkillsCount() {
        return skillsCount;
    }

    public void setSkillsCount(long skillsCount) {
        this.skillsCount = skillsCount;
    }
}
