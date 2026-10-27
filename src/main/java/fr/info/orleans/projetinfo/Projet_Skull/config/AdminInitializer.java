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
           if (playerRepository.findByEmail("admin@projet-skull.com").isEmpty()) {

               Player admin = new Player();
               admin.setEmail("admin@projet-skull.com");
               admin.setMotDePasse("admin123");
               admin.setRole("ADMIN");
               admin.setBanni(false);
               playerRepository.save(admin);

               System.out.println(">>> Compte administrateur créé avec succès au démarrage !");
           } else {
               System.out.println(">>> Le compte administrateur existe déjà");
            }
        };
    }
}