package com.skillswap.skillservice.service;

import com.skillswap.skillservice.dto.PageResponse;
import com.skillswap.skillservice.dto.SkillRequest;
import com.skillswap.skillservice.dto.SkillResponse;
import com.skillswap.skillservice.dto.SkillsStatsResponse;
import com.skillswap.skillservice.exception.ConflictException;
import com.skillswap.skillservice.exception.ResourceNotFoundException;
import com.skillswap.skillservice.model.Category;
import com.skillswap.skillservice.model.Skill;
import com.skillswap.skillservice.model.SkillType;
import com.skillswap.skillservice.repository.CategoryRepository;
import com.skillswap.skillservice.repository.SkillRepository;
import com.skillswap.skillservice.repository.UserSkillRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class SkillService {

    private final SkillRepository skillRepository;
    private final CategoryRepository categoryRepository;
    private final UserSkillRepository userSkillRepository;

    public SkillService(SkillRepository skillRepository,
                        CategoryRepository categoryRepository,
                        UserSkillRepository userSkillRepository) {
        this.skillRepository = skillRepository;
        this.categoryRepository = categoryRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<SkillResponse> searchCatalog(Long categoryId, String q, Pageable pageable) {
        String query = (q != null && !q.trim().isEmpty()) ? q.trim() : null;
        Page<Skill> page = skillRepository.searchCatalog(categoryId, query, pageable);
        Page<SkillResponse> dtoPage = page.map(this::mapToResponse);
        return PageResponse.of(dtoPage);
    }

    @Transactional(readOnly = true)
    public SkillResponse getSkillById(Long id) {
        Skill skill = findSkillOrThrow(id);
        return mapToResponse(skill);
    }

    public SkillResponse createSkill(SkillRequest request) {
        if (skillRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Une compétence nommée '" + request.getName() + "' existe déjà dans le catalogue.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'identifiant " + request.getCategoryId()));

        Skill skill = new Skill(request.getName().trim(), request.getDescription(), category);
        Skill saved = skillRepository.save(skill);
        return mapToResponse(saved);
    }

    public SkillResponse updateSkill(Long id, SkillRequest request) {
        Skill skill = findSkillOrThrow(id);

        if (skillRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new ConflictException("Une autre compétence porte déjà le nom '" + request.getName() + "'.");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'identifiant " + request.getCategoryId()));

        skill.setName(request.getName().trim());
        skill.setDescription(request.getDescription());
        skill.setCategory(category);

        return mapToResponse(skill);
    }

    public void deleteSkill(Long id) {
        Skill skill = findSkillOrThrow(id);
        skillRepository.delete(skill);
    }

    @Transactional(readOnly = true)
    public SkillsStatsResponse getStats() {
        long totalCategories = categoryRepository.count();
        long totalSkills = skillRepository.count();
        long totalUserSkills = userSkillRepository.countByActiveTrue();
        long totalOffered = userSkillRepository.countByTypeAndActiveTrue(SkillType.OFFERED);
        long totalWanted = userSkillRepository.countByTypeAndActiveTrue(SkillType.WANTED);

        Map<String, Long> byCategory = new HashMap<>();
        List<Object[]> grouped = skillRepository.countSkillsGroupedByCategory();
        for (Object[] row : grouped) {
            String catName = (String) row[0];
            Long count = (Long) row[1];
            byCategory.put(catName, count);
        }

        return new SkillsStatsResponse(
            totalCategories,
            totalSkills,
            totalUserSkills,
            totalOffered,
            totalWanted,
            byCategory
        );
    }

    public Skill findSkillOrThrow(Long id) {
        return skillRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Compétence introuvable avec l'identifiant " + id));
    }

    private SkillResponse mapToResponse(Skill skill) {
        return new SkillResponse(
            skill.getId(),
            skill.getName(),
            skill.getDescription(),
            skill.getCategory().getId(),
            skill.getCategory().getName()
        );
    }
}
