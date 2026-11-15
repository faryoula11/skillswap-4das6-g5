package com.skillswap.skillservice.config;

import com.skillswap.skillservice.model.Category;
import com.skillswap.skillservice.model.Skill;
import com.skillswap.skillservice.repository.CategoryRepository;
import com.skillswap.skillservice.repository.SkillRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * Initialisation automatique des données de démonstration du catalogue.
 * Ne s'exécute que si la base est vide pour éviter tout doublon au redémarrage.
 */
@Component
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CategoryRepository categoryRepository;
    private final SkillRepository skillRepository;

    public DataInitializer(CategoryRepository categoryRepository, SkillRepository skillRepository) {
        this.categoryRepository = categoryRepository;
        this.skillRepository = skillRepository;
    }

    @Override
    public void run(String... args) {
        if (categoryRepository.count() > 0) {
            log.info("Le catalogue de compétences contient déjà des données. Initialisation ignorée.");
            return;
        }

        log.info("Base vide : initialisation du catalogue de démonstration SkillSwap...");

        // 1. Catégorie Développement Web
        Category devWeb = new Category("Développement Web", "Technologies, frameworks et langages pour le web");
        categoryRepository.save(devWeb);

        List<Skill> devWebSkills = Arrays.asList(
            new Skill("Spring Boot", "Framework backend Java pour microservices et APIs REST", devWeb),
            new Skill("Angular", "Framework frontend TypeScript pour applications web dynamiques", devWeb),
            new Skill("React", "Bibliothèque JavaScript pour créer des interfaces utilisateur", devWeb),
            new Skill("Docker", "Conteneurisation d'applications et environnements de déploiement", devWeb)
        );
        skillRepository.saveAll(devWebSkills);

        // 2. Catégorie Data & IA
        Category dataAi = new Category("Data & Intelligence Artificielle", "Science des données, bases avancées et modèles IA");
        categoryRepository.save(dataAi);

        List<Skill> dataSkills = Arrays.asList(
            new Skill("Python Data Science", "Analyse de données avec Pandas, NumPy et Matplotlib", dataAi),
            new Skill("Machine Learning", "Algorithmes prédictifs et modélisation avec Scikit-learn", dataAi),
            new Skill("Neo4j & Graphes", "Bases de données orientées graphe et requêtes Cypher", dataAi)
        );
        skillRepository.saveAll(dataSkills);

        // 3. Catégorie Design & Multimédia
        Category design = new Category("Design & Multimédia", "Conception visuelle, ergonomie UI/UX et création média");
        categoryRepository.save(design);

        List<Skill> designSkills = Arrays.asList(
            new Skill("Figma UI/UX", "Conception de maquettes interactives et design systems", design),
            new Skill("Montage Vidéo", "Édition et effets visuels sous Premiere Pro et DaVinci", design)
        );
        skillRepository.saveAll(designSkills);

        // 4. Catégorie Langues Vivantes
        Category langues = new Category("Langues Vivantes", "Apprentissage des langues étrangères et conversation");
        categoryRepository.save(langues);

        List<Skill> languesSkills = Arrays.asList(
            new Skill("Anglais Professionnel", "Préparation aux entretiens, TOEIC et communication d'affaires", langues),
            new Skill("Allemand Débutant", "Bases de la grammaire et vocabulaire du quotidien", langues),
            new Skill("Espagnol Conversationnel", "Échanges oraux et perfectionnement de la prononciation", langues)
        );
        skillRepository.saveAll(languesSkills);

        // 5. Catégorie Musique & Arts
        Category musique = new Category("Musique & Arts", "Pratique instrumentale, solfège et expression artistique");
        categoryRepository.save(musique);

        List<Skill> musiqueSkills = Arrays.asList(
            new Skill("Guitare Acoustique", "Accords fondamentaux, arpèges et rythmiques pop/folk", musique),
            new Skill("Piano & Solfège", "Lecture de partitions et morceaux classiques ou modernes", musique)
        );
        skillRepository.saveAll(musiqueSkills);

        log.info("Catalogue initialisé avec succès : 5 catégories et {} compétences disponibles.", skillRepository.count());
    }
}
