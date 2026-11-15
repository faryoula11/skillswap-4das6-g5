package com.skillswap.skillservice.service;

import com.skillswap.skillservice.dto.PageResponse;
import com.skillswap.skillservice.dto.UserSkillRequest;
import com.skillswap.skillservice.dto.UserSkillResponse;
import com.skillswap.skillservice.exception.ConflictException;
import com.skillswap.skillservice.exception.ResourceNotFoundException;
import com.skillswap.skillservice.model.Skill;
import com.skillswap.skillservice.model.SkillType;
import com.skillswap.skillservice.model.UserSkill;
import com.skillswap.skillservice.repository.UserSkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserSkillService {

    private final UserSkillRepository userSkillRepository;
    private final SkillService skillService;

    public UserSkillService(UserSkillRepository userSkillRepository, SkillService skillService) {
        this.userSkillRepository = userSkillRepository;
        this.skillService = skillService;
    }

    public UserSkillResponse declareUserSkill(String userId, UserSkillRequest request) {
        Skill skill = skillService.findSkillOrThrow(request.getSkillId());

        Optional<UserSkill> existing = userSkillRepository.findByUserIdAndSkillIdAndType(
            userId, request.getSkillId(), request.getType()
        );

        if (existing.isPresent()) {
            UserSkill userSkill = existing.get();
            if (userSkill.isActive()) {
                throw new ConflictException("Vous avez déjà déclaré la compétence '" + skill.getName() +
                    "' en tant que " + request.getType() + ".");
            }
            // Réactivation propre suite à un soft delete antérieur
            userSkill.activate();
            userSkill.setLevel(request.getLevel());
            userSkill.setDescription(request.getDescription());
            UserSkill saved = userSkillRepository.save(userSkill);
            return mapToResponse(saved);
        }

        UserSkill newUserSkill = new UserSkill(
            userId,
            skill,
            request.getType(),
            request.getLevel(),
            request.getDescription()
        );
        UserSkill saved = userSkillRepository.save(newUserSkill);
        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<UserSkillResponse> getMySkills(String userId) {
        return userSkillRepository.findByUserIdAndActiveTrue(userId).stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public UserSkillResponse getUserSkillById(Long id) {
        UserSkill userSkill = findUserSkillOrThrow(id);
        return mapToResponse(userSkill);
    }

    @Transactional(readOnly = true)
    public PageResponse<UserSkillResponse> searchUserSkills(String userId, Long skillId, SkillType type, Pageable pageable) {
        String filterUserId = (userId != null && !userId.trim().isEmpty()) ? userId.trim() : null;
        Page<UserSkill> page = userSkillRepository.searchUserSkills(filterUserId, skillId, type, pageable);
        Page<UserSkillResponse> dtoPage = page.map(this::mapToResponse);
        return PageResponse.of(dtoPage);
    }

    public UserSkillResponse updateUserSkill(Long id, UserSkillRequest request) {
        UserSkill userSkill = findUserSkillOrThrow(id);

        if (!userSkill.isActive()) {
            throw new ResourceNotFoundException("Cette compétence a été désactivée et ne peut pas être modifiée.");
        }

        userSkill.setLevel(request.getLevel());
        userSkill.setDescription(request.getDescription());
        UserSkill saved = userSkillRepository.save(userSkill);
        return mapToResponse(saved);
    }

    public void deactivateUserSkill(Long id) {
        UserSkill userSkill = findUserSkillOrThrow(id);
        userSkill.deactivate();
        userSkillRepository.save(userSkill);
    }

    public UserSkill findUserSkillOrThrow(Long id) {
        return userSkillRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Déclaration de compétence introuvable avec l'identifiant " + id));
    }

    public UserSkillResponse mapToResponse(UserSkill userSkill) {
        return new UserSkillResponse(
            userSkill.getId(),
            userSkill.getUserId(),
            userSkill.getSkill().getId(),
            userSkill.getSkill().getName(),
            userSkill.getSkill().getCategory().getName(),
            userSkill.getType(),
            userSkill.getLevel(),
            userSkill.getDescription(),
            userSkill.isActive(),
            userSkill.getCreatedAt()
        );
    }
}
