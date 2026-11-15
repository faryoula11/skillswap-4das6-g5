package com.skillswap.skillservice.controller;

import com.skillswap.skillservice.dto.PageResponse;
import com.skillswap.skillservice.dto.UserSkillRequest;
import com.skillswap.skillservice.dto.UserSkillResponse;
import com.skillswap.skillservice.model.SkillType;
import com.skillswap.skillservice.model.UserSkill;
import com.skillswap.skillservice.security.SecurityContextHelper;
import com.skillswap.skillservice.service.UserSkillService;
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

import java.util.List;

@RestController
@RequestMapping("/api/user-skills")
@Tag(name = "Compétences des Membres", description = "Déclaration, recherche et gestion des compétences offertes ou recherchées par les membres")
public class UserSkillController {

    private final UserSkillService userSkillService;
    private final SecurityContextHelper securityHelper;

    public UserSkillController(UserSkillService userSkillService, SecurityContextHelper securityHelper) {
        this.userSkillService = userSkillService;
        this.securityHelper = securityHelper;
    }

    @PostMapping
    @Operation(
        summary = "Déclarer une compétence",
        description = "Associe une compétence du catalogue au profil de l'utilisateur connecté (X-User-Id). " +
                      "Si la compétence a été précédemment désactivée (soft delete), elle est réactivée proprement."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Compétence déclarée ou réactivée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides ou en-tête X-User-Id manquant"),
        @ApiResponse(responseCode = "404", description = "Compétence introuvable dans le catalogue"),
        @ApiResponse(responseCode = "409", description = "Compétence déjà active pour ce type")
    })
    public ResponseEntity<UserSkillResponse> declareSkill(@Valid @RequestBody UserSkillRequest request,
                                                          HttpServletRequest httpRequest) {
        String userId = securityHelper.getUserId(httpRequest);
        UserSkillResponse response = userSkillService.declareUserSkill(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    @Operation(summary = "Consulter mes compétences déclarées", description = "Renvoie la liste des compétences actives de l'utilisateur connecté.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Liste des compétences du membre"),
        @ApiResponse(responseCode = "400", description = "En-tête X-User-Id manquant")
    })
    public ResponseEntity<List<UserSkillResponse>> getMySkills(HttpServletRequest httpRequest) {
        String userId = securityHelper.getUserId(httpRequest);
        return ResponseEntity.ok(userSkillService.getMySkills(userId));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Consulter une compétence par son identifiant",
        description = "Endpoint clé appelé notamment par exchange-service via OpenFeign avant de valider un échange. " +
                      "Renvoie l'état complet (userId, skillId, type, level, active)."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compétence trouvée"),
        @ApiResponse(responseCode = "404", description = "Déclaration introuvable")
    })
    public ResponseEntity<UserSkillResponse> getUserSkillById(@PathVariable Long id) {
        return ResponseEntity.ok(userSkillService.getUserSkillById(id));
    }

    @GetMapping
    @Operation(
        summary = "Rechercher parmi les compétences des membres (paginé)",
        description = "Permet de répondre à 'qui enseigne telle compétence ?' (skillId) ou de voir les compétences d'un membre (userId) filtrées par type (OFFERED/WANTED)."
    )
    @ApiResponse(responseCode = "200", description = "Page de résultats retournée avec succès")
    public ResponseEntity<PageResponse<UserSkillResponse>> searchUserSkills(
        @Parameter(description = "Filtrer par identifiant utilisateur (optionnel)")
        @RequestParam(required = false) String userId,

        @Parameter(description = "Filtrer par compétence (ex: qui enseigne Java ?)")
        @RequestParam(required = false) Long skillId,

        @Parameter(description = "Filtrer par type : OFFERED ou WANTED (optionnel)")
        @RequestParam(required = false) SkillType type,

        @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        return ResponseEntity.ok(userSkillService.searchUserSkills(userId, skillId, type, pageable));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une compétence déclarée", description = "Réservé au propriétaire de la compétence.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Compétence mise à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Propriétaire uniquement"),
        @ApiResponse(responseCode = "404", description = "Compétence introuvable ou inactive")
    })
    public ResponseEntity<UserSkillResponse> updateUserSkill(@PathVariable Long id,
                                                             @Valid @RequestBody UserSkillRequest request,
                                                             HttpServletRequest httpRequest) {
        UserSkill existing = userSkillService.findUserSkillOrThrow(id);
        securityHelper.requireOwnerOrAdmin(httpRequest, existing.getUserId());
        return ResponseEntity.ok(userSkillService.updateUserSkill(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Désactiver une compétence (Suppression logique / Soft delete)",
        description = "Passe le statut à active = false sans supprimer la ligne en base. " +
                      "Préserve ainsi l'historique des échanges dans exchange-service."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Compétence désactivée avec succès"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Propriétaire ou ADMIN uniquement"),
        @ApiResponse(responseCode = "404", description = "Compétence introuvable")
    })
    public ResponseEntity<Void> deleteUserSkill(@PathVariable Long id, HttpServletRequest httpRequest) {
        UserSkill existing = userSkillService.findUserSkillOrThrow(id);
        securityHelper.requireOwnerOrAdmin(httpRequest, existing.getUserId());
        userSkillService.deactivateUserSkill(id);
        return ResponseEntity.noContent().build();
    }
}
