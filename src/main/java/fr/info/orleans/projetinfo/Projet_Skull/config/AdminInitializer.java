package fr.info.orleans.projetinfo.Projet_Skull.config;

import fr.info.orleans.projetinfo.Projet_Skull.model.Player;
import fr.info.orleans.projetinfo.Projet_Skull.repository.PlayerRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner initAdmin(PlayerRepository playerRepository) {
        return args -> {
            // On vérifie si un admin existe déjà
            boolean adminExists = playerRepository.findAll().stream()
                    .anyMatch(player -> "ADMIN".equalsIgnoreCase(player.getRole()));

            if (!adminExists) {
                Player admin = new Player();
                admin.setEmail("admin@projet-skull.com");
                admin.setMotDePasse("admin123");
                admin.setRole("ADMIN");

                playerRepository.save(admin);
                System.out.println(">>> Compte administrateur créé avec succès au démarrage !");
            }
        };
    }
}