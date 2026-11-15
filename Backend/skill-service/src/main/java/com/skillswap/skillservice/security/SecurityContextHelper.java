package com.skillswap.skillservice.security;

import com.skillswap.skillservice.exception.BadRequestException;
import com.skillswap.skillservice.exception.ForbiddenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Gestionnaire d'identité utilisateur pour le skill-service.
 *
 * En phase de développement et de tests Swagger :
 * Lit l'identifiant du membre dans l'en-tête "X-User-Id"
 * et les rôles dans l'en-tête "X-User-Roles" (ex: "USER,ADMIN").
 *
 * Lors de l'intégration avec Keycloak (séance 8), cette classe
 * sera adaptée pour extraire les claims directement du jeton JWT.
 */
@Component
public class SecurityContextHelper {

    public static final String HEADER_USER_ID = "X-User-Id";
    public static final String HEADER_USER_ROLES = "X-User-Roles";

    public String getUserId(HttpServletRequest request) {
        String userId = request.getHeader(HEADER_USER_ID);
        if (userId == null || userId.trim().isEmpty()) {
            throw new BadRequestException("L'en-tête obligatoire '" + HEADER_USER_ID + "' est manquant. " +
                "Veuillez renseigner votre identifiant utilisateur (ex: user-123).");
        }
        return userId.trim();
    }

    public Set<String> getRoles(HttpServletRequest request) {
        String rolesHeader = request.getHeader(HEADER_USER_ROLES);
        if (rolesHeader == null || rolesHeader.trim().isEmpty()) {
            return Collections.singleton("USER"); // rôle par défaut
        }
        return Arrays.stream(rolesHeader.split(","))
            .map(String::trim)
            .map(String::toUpperCase)
            .collect(Collectors.toSet());
    }

    public boolean isAdmin(HttpServletRequest request) {
        return getRoles(request).contains("ADMIN");
    }

    public void requireAdmin(HttpServletRequest request) {
        if (!isAdmin(request)) {
            throw new ForbiddenException("Accès refusé : rôle ADMIN requis pour cette opération.");
        }
    }

    public void requireOwnerOrAdmin(HttpServletRequest request, String ownerUserId) {
        String currentUserId = getUserId(request);
        if (!currentUserId.equals(ownerUserId) && !isAdmin(request)) {
            throw new ForbiddenException("Accès refusé : vous n'êtes ni le propriétaire de cette ressource ni administrateur.");
        }
    }
}
