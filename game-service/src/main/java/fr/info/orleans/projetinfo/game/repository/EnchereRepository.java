package fr.info.orleans.projetinfo.game.repository;

import fr.info.orleans.projetinfo.game.model.Enchere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EnchereRepository extends JpaRepository<Enchere, Long> {
    List<Enchere> findByIdPartieAndMancheOrderByDateEnchereAsc(Long idPartie, int manche);

    Optional<Enchere> findFirstByIdPartieAndMancheAndRetireFalseOrderByValeurDesc(Long idPartie, int manche);

    long countByIdPartieAndMancheAndRetireTrue(Long idPartie, int manche);
}