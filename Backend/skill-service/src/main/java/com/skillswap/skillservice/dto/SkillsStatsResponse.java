package com.skillswap.skillservice.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Statistiques globales du catalogue et des compétences")
public class SkillsStatsResponse {

    @Schema(description = "Nombre total de catégories", example = "6")
    private long totalCategories;

    @Schema(description = "Nombre total de compétences dans le catalogue", example = "24")
    private long totalSkills;

    @Schema(description = "Nombre total de déclarations actives", example = "15")
    private long totalUserSkills;

    @Schema(description = "Nombre de compétences offertes (OFFERED)", example = "10")
    private long totalOffered;

    @Schema(description = "Nombre de compétences recherchées (WANTED)", example = "5")
    private long totalWanted;

    @Schema(description = "Répartition des compétences par catégorie")
    private Map<String, Long> skillsByCategory;

    public SkillsStatsResponse() {
    }

    public SkillsStatsResponse(long totalCategories, long totalSkills, long totalUserSkills,
                               long totalOffered, long totalWanted, Map<String, Long> skillsByCategory) {
        this.totalCategories = totalCategories;
        this.totalSkills = totalSkills;
        this.totalUserSkills = totalUserSkills;
        this.totalOffered = totalOffered;
        this.totalWanted = totalWanted;
        this.skillsByCategory = skillsByCategory;
    }

    public long getTotalCategories() {
        return totalCategories;
    }

    public void setTotalCategories(long totalCategories) {
        this.totalCategories = totalCategories;
    }

    public long getTotalSkills() {
        return totalSkills;
    }

    public void setTotalSkills(long totalSkills) {
        this.totalSkills = totalSkills;
    }

    public long getTotalUserSkills() {
        return totalUserSkills;
    }

    public void setTotalUserSkills(long totalUserSkills) {
        this.totalUserSkills = totalUserSkills;
    }

    public long getTotalOffered() {
        return totalOffered;
    }

    public void setTotalOffered(long totalOffered) {
        this.totalOffered = totalOffered;
    }

    public long getTotalWanted() {
        return totalWanted;
    }

    public void setTotalWanted(long totalWanted) {
        this.totalWanted = totalWanted;
    }

    public Map<String, Long> getSkillsByCategory() {
        return skillsByCategory;
    }

    public void setSkillsByCategory(Map<String, Long> skillsByCategory) {
        this.skillsByCategory = skillsByCategory;
    }
}
