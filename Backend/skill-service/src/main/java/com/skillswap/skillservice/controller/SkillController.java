package com.skillswap.skillservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Point d'entrée de test du skill-service.
 * La logique métier (catalogue, compétences des membres) sera ajoutée
 * après la validation de la conception (séance 4).
 */
@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Catalogue des compétences")
public class SkillController {

    @GetMapping
    @Operation(summary = "Consulter le catalogue des compétences",
               description = "Renvoie pour l'instant une liste vide : la logique métier n'est pas encore développée.")
    public List<Object> getSkills() {
        return List.of();
    }
}
