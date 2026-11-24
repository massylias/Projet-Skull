package fr.info.orleans.projetinfo.game.repository;

import fr.info.orleans.projetinfo.game.model.Participation;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findByIdPartieOrderByPositionTableAsc(Long idPartie);

    Optional<Participation> findByIdPartieAndIdJoueur(Long idPartie, Long idJoueur);

    long countByIdPartie(Long idPartie);

    boolean existsByIdPartieAndIdJoueur(Long idPartie, Long idJoueur);

    List<Participation> findByIdJoueur(Long idJoueur);
}