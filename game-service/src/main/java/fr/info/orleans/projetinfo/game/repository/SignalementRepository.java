package fr.info.orleans.projetinfo.game.repository;

import fr.info.orleans.projetinfo.game.model.Signalement;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SignalementRepository extends JpaRepository<Signalement, Long> {
    List<Signalement> findAllByOrderByDateSignalementDesc();

    List<Signalement> findByTraiteOrderByDateSignalementDesc(boolean traite);

    List<Signalement> findByIdJoueurSignale(Long idJoueurSignale);

    boolean existsByIdPartieAndIdJoueurSignaleAndIdJoueurSignaleur(
            Long idPartie, Long idJoueurSignale, Long idJoueurSignaleur);
}