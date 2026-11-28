package fr.info.orleans.projetinfo.game.repository;

import fr.info.orleans.projetinfo.game.model.Tuile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TuileRepository extends JpaRepository<Tuile,Long> {
    List<Tuile> findByIdPartieAndIdJoueurOrderByPositionAsc(Long idPartie, Long idJoueur);
    List<Tuile> findByIdPartie(Long idPartie);

}
