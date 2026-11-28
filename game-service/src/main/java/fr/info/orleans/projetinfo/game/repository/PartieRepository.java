package fr.info.orleans.projetinfo.game.repository;

import fr.info.orleans.projetinfo.game.model.Partie;
import fr.info.orleans.projetinfo.game.model.StatutPartie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PartieRepository extends JpaRepository<Partie, Long> {
    List<Partie> findByStatutOrderByDateCreationDesc(StatutPartie statut);

    List<Partie> findByIdCreateur(Long idCreateur);
}