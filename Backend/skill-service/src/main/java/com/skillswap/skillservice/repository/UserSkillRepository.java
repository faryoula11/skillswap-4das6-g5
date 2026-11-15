package com.skillswap.skillservice.repository;

import com.skillswap.skillservice.model.SkillType;
import com.skillswap.skillservice.model.UserSkill;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserSkillRepository extends JpaRepository<UserSkill, Long> {

    Optional<UserSkill> findByUserIdAndSkillIdAndType(String userId, Long skillId, SkillType type);

    List<UserSkill> findByUserIdAndActiveTrue(String userId);

    long countByActiveTrue();

    long countByTypeAndActiveTrue(SkillType type);

    @Query("SELECT us FROM UserSkill us WHERE " +
           "(:userId IS NULL OR us.userId = :userId) AND " +
           "(:skillId IS NULL OR us.skill.id = :skillId) AND " +
           "(:type IS NULL OR us.type = :type) AND " +
           "(us.active = true)")
    Page<UserSkill> searchUserSkills(@Param("userId") String userId,
                                     @Param("skillId") Long skillId,
                                     @Param("type") SkillType type,
                                     Pageable pageable);
}
