package fr.info.orleans.projetinfo.game.repository;

import fr.info.orleans.projetinfo.game.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByIdPartieOrderByPositionAsc(Long idPartie);

    Optional<Participation> findByIdPartieAndIdJoueur(Long idPartie, Long idJoueur);

    long countByIdPartie(Long idPartie);

    boolean existsByIdPartieAndIdJoueur(Long idPartie, Long idJoueur);

    List<Participation> findByIdJoueur(Long idJoueur);

    // Compter les IA
    long countByIdPartieAndEstIaTrue(Long idPartie);
}