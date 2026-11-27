package fr.info.orleans.projetinfo.ladder.repository;

import fr.info.orleans.projetinfo.ladder.model.Historique;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoriqueRepository extends JpaRepository<Historique, Long> {
    List<Historique> findByIdJoueur(Long idJoueur);
    List<Historique> findByIdPartie(Long idPartie);
    boolean existsByIdPartieAndIdJoueur(Long idPartie, Long idJoueur);
}