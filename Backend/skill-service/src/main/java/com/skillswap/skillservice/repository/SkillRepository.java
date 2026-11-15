package com.skillswap.skillservice.repository;

import com.skillswap.skillservice.model.Skill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    boolean existsByNameIgnoreCase(String name);

    boolean existsByNameIgnoreCaseAndIdNot(String name, Long id);

    Optional<Skill> findByNameIgnoreCase(String name);

    long countByCategoryId(Long categoryId);

    @Query("SELECT s FROM Skill s WHERE " +
           "(:categoryId IS NULL OR s.category.id = :categoryId) AND " +
           "(:q IS NULL OR :q = '' OR LOWER(s.name) LIKE LOWER(CONCAT('%', :q, '%')) OR LOWER(s.description) LIKE LOWER(CONCAT('%', :q, '%')))")
    Page<Skill> searchCatalog(@Param("categoryId") Long categoryId, @Param("q") String q, Pageable pageable);

    @Query("SELECT s.category.name, COUNT(s) FROM Skill s GROUP BY s.category.name")
    List<Object[]> countSkillsGroupedByCategory();
}
