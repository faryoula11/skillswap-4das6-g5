package com.skillswap.skillservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.skillswap.skillservice.dto.CategoryRequest;
import com.skillswap.skillservice.dto.SkillRequest;
import com.skillswap.skillservice.dto.UserSkillRequest;
import com.skillswap.skillservice.model.Skill;
import com.skillswap.skillservice.model.SkillLevel;
import com.skillswap.skillservice.model.SkillType;
import com.skillswap.skillservice.repository.SkillRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class SkillServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private SkillRepository skillRepository;

    @Test
    @DisplayName("GET /api/categories - Doit retourner les catégories de démonstration (200 OK)")
    void shouldReturnInitialCategories() throws Exception {
        mockMvc.perform(get("/api/categories"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", not(empty())))
            .andExpect(jsonPath("$[*].name", hasItem("Développement Web")));
    }

    @Test
    @DisplayName("GET /api/skills - Doit rechercher dans le catalogue de compétences (200 OK)")
    void shouldSearchSkillsInCatalog() throws Exception {
        mockMvc.perform(get("/api/skills")
                .param("q", "Spring"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.content", not(empty())))
            .andExpect(jsonPath("$.content[0].name", is("Spring Boot")));
    }

    @Test
    @DisplayName("POST /api/categories - Doit refuser la création si l'utilisateur n'est pas ADMIN (403 Forbidden)")
    void shouldForbidCategoryCreationForNonAdmin() throws Exception {
        CategoryRequest request = new CategoryRequest("Nouvelle Catégorie", "Description");

        mockMvc.perform(post("/api/categories")
                .header("X-User-Roles", "USER")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andExpect(jsonPath("$.status", is(403)))
            .andExpect(jsonPath("$.title", is("Accès interdit")));
    }

    @Test
    @DisplayName("POST /api/categories - Doit créer la catégorie si l'utilisateur est ADMIN (201 Created)")
    void shouldCreateCategoryForAdmin() throws Exception {
        CategoryRequest request = new CategoryRequest("Cuisine & Pâtisserie", "Art culinaire et recettes du monde");

        mockMvc.perform(post("/api/categories")
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.name", is("Cuisine & Pâtisserie")));
    }

    @Test
    @DisplayName("POST /api/categories - Doit rejeter un doublon de nom de catégorie (409 Conflict)")
    void shouldRejectDuplicateCategoryName() throws Exception {
        CategoryRequest duplicate = new CategoryRequest("Développement Web", "Tentative de doublon");

        mockMvc.perform(post("/api/categories")
                .header("X-User-Roles", "ADMIN")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(duplicate)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)))
            .andExpect(jsonPath("$.title", is("Conflit de données")));
    }

    @Test
    @DisplayName("POST /api/user-skills & Cycle complet - Déclaration, doublon 409, soft delete et réactivation")
    void shouldHandleCompleteUserSkillLifecycle() throws Exception {
        Skill springBoot = skillRepository.findByNameIgnoreCase("Spring Boot")
            .orElseThrow(() -> new IllegalStateException("Skill 'Spring Boot' non trouvée"));

        String testUserId = "user-test-lifecycle-1";
        UserSkillRequest request = new UserSkillRequest(
            springBoot.getId(),
            SkillType.OFFERED,
            SkillLevel.INTERMEDIATE,
            "Expérience pratique avec Spring Boot 3"
        );

        // 1. Création initiale (201 Created)
        String responseContent = mockMvc.perform(post("/api/user-skills")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", notNullValue()))
            .andExpect(jsonPath("$.userId", is(testUserId)))
            .andExpect(jsonPath("$.skillName", is("Spring Boot")))
            .andExpect(jsonPath("$.active", is(true)))
            .andReturn().getResponse().getContentAsString();

        Long userSkillId = objectMapper.readTree(responseContent).get("id").asLong();

        // 2. Vérification de l'endpoint OpenFeign GET /api/user-skills/{id} (200 OK)
        mockMvc.perform(get("/api/user-skills/{id}", userSkillId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId", is(testUserId)))
            .andExpect(jsonPath("$.skillId", is(springBoot.getId().intValue())))
            .andExpect(jsonPath("$.type", is("OFFERED")))
            .andExpect(jsonPath("$.active", is(true)));

        // 3. Tentative de doublon direct (409 Conflict)
        mockMvc.perform(post("/api/user-skills")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.status", is(409)));

        // 4. Consultation de mes compétences GET /api/user-skills/me
        mockMvc.perform(get("/api/user-skills/me")
                .header("X-User-Id", testUserId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))));

        // 5. Soft delete DELETE /api/user-skills/{id} (204 No Content)
        mockMvc.perform(delete("/api/user-skills/{id}", userSkillId)
                .header("X-User-Id", testUserId))
            .andExpect(status().isNoContent());

        // Vérification que le statut est passé à active = false
        mockMvc.perform(get("/api/user-skills/{id}", userSkillId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.active", is(false)));

        // 6. Réactivation : redéclaration de la même compétence -> réactive l'enregistrement existant
        UserSkillRequest reactivateRequest = new UserSkillRequest(
            springBoot.getId(),
            SkillType.OFFERED,
            SkillLevel.EXPERT,
            "Niveau mis à jour après perfectionnement"
        );

        mockMvc.perform(post("/api/user-skills")
                .header("X-User-Id", testUserId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(reactivateRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id", is(userSkillId.intValue()))) // même identifiant réactivé
            .andExpect(jsonPath("$.active", is(true)))
            .andExpect(jsonPath("$.level", is("EXPERT")));
    }

    @Test
    @DisplayName("GET /api/skills/stats - Doit retourner les statistiques pour un ADMIN (200 OK)")
    void shouldReturnSkillsStatsForAdmin() throws Exception {
        mockMvc.perform(get("/api/skills/stats")
                .header("X-User-Roles", "ADMIN"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.totalCategories", greaterThan(0)))
            .andExpect(jsonPath("$.totalSkills", greaterThan(0)))
            .andExpect(jsonPath("$.skillsByCategory", notNullValue()));
    }
}
