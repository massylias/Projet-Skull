package fr.info.orleans.projetinfo.Projet_Skull.repository;

import fr.info.orleans.projetinfo.Projet_Skull.model.Player;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {

    //Spring Boot génere automatiquement les methode de base de données standard comme .save,  .FindAll(), delete() etc...


    Optional<Player> findByEmail(String email);

    boolean existsByEmail(String email);
}