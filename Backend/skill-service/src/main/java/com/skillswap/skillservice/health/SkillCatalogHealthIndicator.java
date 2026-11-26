package com.skillswap.skillservice.health;

import com.skillswap.skillservice.repository.CategoryRepository;
import com.skillswap.skillservice.repository.SkillRepository;
import com.skillswap.skillservice.repository.UserSkillRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

/**
 * HealthIndicator personnalisé Spring Boot Actuator (Atelier Observabilité).
 * Expose l'état du catalogue de compétences dans /actuator/health.
 */
@Component("skillCatalog")
public class SkillCatalogHealthIndicator implements HealthIndicator {

    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;
    private final UserSkillRepository userSkillRepository;

    public SkillCatalogHealthIndicator(CategoryRepository categoryRepository,
                                       SkillRepository skillRepository,
                                       UserSkillRepository userSkillRepository) {
        this.categoryRepository = categoryRepository;
        this.skillRepository = skillRepository;
        this.userSkillRepository = userSkillRepository;
    }

    @Override
    public Health health() {
        try {
            long categoriesCount = categoryRepository.count();
            long skillsCount = skillRepository.count();
            long userSkillsCount = userSkillRepository.count();

            Health.Builder builder = Health.up()
                    .withDetail("database", "MySQL (skill_db)")
                    .withDetail("categoriesCount", categoriesCount)
                    .withDetail("skillsCount", skillsCount)
                    .withDetail("userSkillsCount", userSkillsCount);

            if (skillsCount == 0) {
                builder.withDetail("catalogState", "EMPTY");
            } else {
                builder.withDetail("catalogState", "READY");
            }

            return builder.build();
        } catch (Exception ex) {
            return Health.down(ex)
                    .withDetail("database", "MySQL (skill_db)")
                    .withDetail("catalogState", "UNAVAILABLE")
                    .build();
        }
    }
}
