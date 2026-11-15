package com.skillswap.skillservice.controller;

import com.skillswap.skillservice.dto.CategoryRequest;
import com.skillswap.skillservice.dto.CategoryResponse;
import com.skillswap.skillservice.security.SecurityContextHelper;
import com.skillswap.skillservice.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@Tag(name = "Catégories", description = "Administration et consultation des catégories du catalogue")
public class CategoryController {

    private final CategoryService categoryService;
    private final SecurityContextHelper securityHelper;

    public CategoryController(CategoryService categoryService, SecurityContextHelper securityHelper) {
        this.categoryService = categoryService;
        this.securityHelper = securityHelper;
    }

    @GetMapping
    @Operation(summary = "Lister toutes les catégories", description = "Accès public : renvoie toutes les catégories avec le nombre de compétences associées.")
    @ApiResponse(responseCode = "200", description = "Liste des catégories récupérée avec succès")
    public ResponseEntity<List<CategoryResponse>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Consulter une catégorie par son identifiant", description = "Accès public.")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Catégorie trouvée"),
        @ApiResponse(responseCode = "404", description = "Catégorie introuvable")
    })
    public ResponseEntity<CategoryResponse> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }

    @PostMapping
    @Operation(summary = "Créer une nouvelle catégorie", description = "Réservé à l'ADMIN (X-User-Roles: ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Catégorie créée avec succès"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Accès refusé - Rôle ADMIN requis"),
        @ApiResponse(responseCode = "409", description = "Une catégorie avec ce nom existe déjà")
    })
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request,
                                                           HttpServletRequest httpRequest) {
        securityHelper.requireAdmin(httpRequest);
        CategoryResponse response = categoryService.createCategory(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modifier une catégorie", description = "Réservé à l'ADMIN (X-User-Roles: ADMIN).")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Catégorie mise à jour"),
        @ApiResponse(responseCode = "400", description = "Données invalides"),
        @ApiResponse(responseCode = "403", description = "Rôle ADMIN requis"),
        @ApiResponse(responseCode = "404", description = "Catégorie introuvable"),
        @ApiResponse(responseCode = "409", description = "Nom déjà utilisé par une autre catégorie")
    })
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable Long id,
                                                           @Valid @RequestBody CategoryRequest request,
                                                           HttpServletRequest httpRequest) {
        securityHelper.requireAdmin(httpRequest);
        return ResponseEntity.ok(categoryService.updateCategory(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer une catégorie", description = "Réservé à l'ADMIN. Refusé si la catégorie contient des compétences.")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Catégorie supprimée"),
        @ApiResponse(responseCode = "403", description = "Rôle ADMIN requis"),
        @ApiResponse(responseCode = "404", description = "Catégorie introuvable"),
        @ApiResponse(responseCode = "409", description = "Catégorie non vide")
    })
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, HttpServletRequest httpRequest) {
        securityHelper.requireAdmin(httpRequest);
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
