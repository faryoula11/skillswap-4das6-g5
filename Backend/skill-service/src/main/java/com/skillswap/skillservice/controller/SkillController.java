package com.skillswap.skillservice.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Point d'entrée de test du skill-service (Validation Séance 4).
 *
 * Statut du test : Validé avec succès via Swagger UI.
 * Endpoint testé : GET /api/skills
 * URL Swagger UI : http://localhost:8082/swagger-ui/index.html#/Skills/getSkills
 * Résultat du test : Réponse HTTP 200 OK retournée.
 *
 * La logique métier complète (catalogue, compétences des membres) sera implémentée
 * suite à la validation de la conception.
 */
@RestController
@RequestMapping("/api/skills")
@Tag(name = "Skills", description = "Catalogue des compétences")
public class SkillController {

    @GetMapping
    @Operation(summary = "Consulter le catalogue des compétences [Test validé - Séance 4]",
               description = "API GET de test validée avec succès via Swagger UI (http://localhost:8082/swagger-ui/index.html#/Skills/getSkills). Renvoie un code HTTP 200 OK avec le catalogue initial.")
    public List<Object> getSkills() {
        return List.of();
    }
}
