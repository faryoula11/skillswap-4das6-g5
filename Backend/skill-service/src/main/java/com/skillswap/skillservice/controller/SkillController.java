package com.skillswap.skillservice.controller;

import com.skillswap.skillservice.dto.PageResponse;
import com.skillswap.skillservice.dto.SkillRequest;
import com.skillswap.skillservice.dto.SkillResponse;
import com.skillswap.skillservice.dto.SkillsStatsResponse;
import com.skillswap.skillservice.security.SecurityContextHelper;
import com.skillswap.skillservice.service.SkillService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/skills")
@Tag(name = "Catalogue", description = "Consultation publique et administration du catalogue des compétences")
public class SkillController {

    private final SkillService skillService;
    private final SecurityContextHelper securityHelper;

    public SkillController(SkillService skillService, SecurityContextHelper securityHelper) {
        this.skillService = skillService;
        this.securityHelper = securityHelper;
    }

    @GetMapping
    @Operation(
        summary = "Consulter et rechercher dans le catalogue de compétences",
        description = "Accès public. Recherche paginée par mot-clé (q) et filtrage par catégorie (categoryId)."
    )
    @ApiResponse(responseCode = "200", description = "Page de compétences retournée avec succès")
    public ResponseEntity<PageResponse<SkillResponse>> searchSkills(
        @Parameter(description = "Filtrer par catégorie (optionnel)")
        @RequestParam(required = false) Long categoryId,

        @Parameter(description = "Recherche textuelle sur le nom ou la description (optionnel)")
        @RequestParam(required = false) String q,

        @PageableDefault(size = 10, sort = "name", direction = Sort.Direction.ASC) Pageable pageable
    ) {
        return ResponseEntity.ok(skillService.searchCatalog(categoryId, q, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une compétence par son identifiant", description = "Accès public.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compétence trouvée"),
        @ApiResponse(responseCode = "404", description = "Compétence introuvable")
    })
    public ResponseEntity<SkillResponse> getSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(skillService.getSkillById(id));
    }

    @PostMapping
    @Operation(summary = "Ajouter une compétence au catalogue", description = "Réservé à l'ADMIN (X-User-Roles: ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compétence créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Rôle ADMIN requis"),
        @ApiResponse(responseCode = "404", description = "Catégorie parente introuvable"),
        @ApiResponse(responseCode = "409", description = "Nom de compétence déjà utilisé")
    })
    public ResponseEntity<SkillResponse> createSkill(@Valid @RequestBody SkillRequest request,
                                                     HttpServletRequest httpRequest) {
        securityHelper.requireAdmin(httpRequest);
        SkillResponse response = skillService.createSkill(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une compétence du catalogue", description = "Réservé à l'ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compétence mise à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Rôle ADMIN requis"),
        @ApiResponse(responseCode = "404", description = "Compétence ou catégorie introuvable"),
        @ApiResponse(responseCode = "409", description = "Nom déjà utilisé")
    })
    public ResponseEntity<SkillResponse> updateSkill(@PathVariable Long id,
                                                     @Valid @RequestBody SkillRequest request,
                                                     HttpServletRequest httpRequest) {
        securityHelper.requireAdmin(httpRequest);
        return ResponseEntity.ok(skillService.updateSkill(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une compétence du catalogue", description = "Réservé à l'ADMIN.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Compétence supprimée"),
        @ApiResponse(responseCode = "403", description = "Rôle ADMIN requis"),
        @ApiResponse(responseCode = "404", description = "Compétence introuvable")
    })
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id, HttpServletRequest httpRequest) {
        securityHelper.requireAdmin(httpRequest);
        skillService.deleteSkill(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/stats")
    @Operation(
        summary = "Statistiques globales du catalogue et des compétences",
        description = "Réservé à l'ADMIN : nombre total de catégories, compétences, compétences offertes vs recherchées."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Statistiques calculées"),
        @ApiResponse(responseCode = "403", description = "Rôle ADMIN requis")
    })
    public ResponseEntity<SkillsStatsResponse> getStats(HttpServletRequest httpRequest) {
        securityHelper.requireAdmin(httpRequest);
        return ResponseEntity.ok(skillService.getStats());
    }
}
