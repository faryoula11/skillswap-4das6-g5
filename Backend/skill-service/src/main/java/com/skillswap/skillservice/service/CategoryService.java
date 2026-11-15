package com.skillswap.skillservice.service;

import com.skillswap.skillservice.dto.CategoryRequest;
import com.skillswap.skillservice.dto.CategoryResponse;
import com.skillswap.skillservice.exception.ConflictException;
import com.skillswap.skillservice.exception.ResourceNotFoundException;
import com.skillswap.skillservice.model.Category;
import com.skillswap.skillservice.repository.CategoryRepository;
import com.skillswap.skillservice.repository.SkillRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public CategoryService(CategoryRepository categoryRepository, SkillRepository skillRepository) {
        this.categoryRepository = categoryRepository;
        this.skillRepository = skillRepository;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAll().stream()
            .map(this::mapToResponse)
            .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryResponse getCategoryById(Long id) {
        Category category = findCategoryOrThrow(id);
        return mapToResponse(category);
    }

    public CategoryResponse createCategory(CategoryRequest request) {
        if (categoryRepository.existsByNameIgnoreCase(request.getName())) {
            throw new ConflictException("Une catégorie avec le nom '" + request.getName() + "' existe déjà.");
        }

        Category category = new Category(request.getName().trim(), request.getDescription());
        Category saved = categoryRepository.save(category);
        return mapToResponse(saved);
    }

    public CategoryResponse updateCategory(Long id, CategoryRequest request) {
        Category category = findCategoryOrThrow(id);

        if (categoryRepository.existsByNameIgnoreCaseAndIdNot(request.getName(), id)) {
            throw new ConflictException("Une autre catégorie porte déjà le nom '" + request.getName() + "'.");
        }

        category.setName(request.getName().trim());
        category.setDescription(request.getDescription());
        return mapToResponse(category);
    }

    public void deleteCategory(Long id) {
        Category category = findCategoryOrThrow(id);
        long skillsCount = skillRepository.countByCategoryId(id);
        if (skillsCount > 0) {
            throw new ConflictException("Impossible de supprimer la catégorie '" + category.getName() +
                "' car elle contient encore " + skillsCount + " compétence(s).");
        }
        categoryRepository.delete(category);
    }

    public Category findCategoryOrThrow(Long id) {
        return categoryRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Catégorie introuvable avec l'identifiant " + id));
    }

    private CategoryResponse mapToResponse(Category category) {
        long count = skillRepository.countByCategoryId(category.getId());
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            count
        );
    }
}
