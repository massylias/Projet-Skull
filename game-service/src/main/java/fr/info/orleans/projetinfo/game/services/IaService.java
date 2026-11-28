package fr.info.orleans.projetinfo.game.services;

import fr.info.orleans.projetinfo.game.model.*;
import fr.info.orleans.projetinfo.game.repository.EnchereRepository;
import fr.info.orleans.projetinfo.game.repository.ParticipationRepository;
import fr.info.orleans.projetinfo.game.repository.PartieRepository;
import fr.info.orleans.projetinfo.game.repository.TuileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class IaService {
    private final PartieRepository partieRepository;
    private final ParticipationRepository participationRepository;
    private final TuileRepository tuileRepository;
    private final EnchereRepository enchereRepository;
    private final Random random = new Random();

    public IaService(PartieRepository partieRepository,
                     ParticipationRepository participationRepository,
                     TuileRepository tuileRepository,
                     EnchereRepository enchereRepository) {
        this.partieRepository = partieRepository;
        this.participationRepository = participationRepository;
        this.tuileRepository = tuileRepository;
        this.enchereRepository = enchereRepository;
    }

    @Transactional
    public void jouerTour(Long idPartie, Long idJoueurIa) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_COURS) return;

        // 1. Si l'IA doit poser une tuile (phase POSE)
        if (partie.getPhase() == PhaseDeJeu.POSE) {
            // 80% de chance de poser, 20% d'enchérir
            if (random.nextInt(100) < 80) {
                poserTuile(idPartie, idJoueurIa);
            } else {
                // L'IA enchérit (elle annonce son nombre de tuiles restantes)
                encherir(idPartie, idJoueurIa);
            }
        }
        // 2. Si l'IA doit enchérir ou se retirer (phase ENCHERE)
        else if (partie.getPhase() == PhaseDeJeu.ENCHERE) {
            // Vérifier si l'IA a posé un crâne
            List<Tuile> tuilesPosees = tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(idPartie, idJoueurIa)
                    .stream().filter(t -> !t.isFaceCachee()).toList();
            boolean aPoseCrane = tuilesPosees.stream().anyMatch(t -> t.getType() == TypeTuile.CRANE);

            int chanceSurenchere = aPoseCrane ? 20 : 70; // 20% si crâne, 70% si fleurs

            if (random.nextInt(100) < chanceSurenchere) {
                // Surenchérir (+1 par rapport à l'enchère max)
                int nouvelleEnchere = (partie.getEnchereMax() == null ? 1 : partie.getEnchereMax() + 1);
                encherir(idPartie, idJoueurIa, nouvelleEnchere);
            } else {
                // Se retirer
                seRetirer(idPartie, idJoueurIa);
            }
        }
    }

    private void poserTuile(Long idPartie, Long idJoueur) {
        List<Tuile> tuiles = tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(idPartie, idJoueur);
        Tuile tuileAPoser = tuiles.stream().filter(Tuile::isFaceCachee).findFirst().orElse(null);

        if (tuileAPoser != null) {
            tuileAPoser.setFaceCachee(false);
            tuileRepository.save(tuileAPoser);

            Participation participation = participationRepository.findByIdPartieAndIdJoueur(idPartie, idJoueur).get();
            participation.setTuilesRestantes(participation.getTuilesRestantes() - 1);
            participationRepository.save(participation);
        }
    }

    private void encherir(Long idPartie, Long idJoueur) {
        Partie partie = partieRepository.findById(idPartie).get();
        encherir(idPartie, idJoueur, (partie.getEnchereMax() == null ? 1 : partie.getEnchereMax() + 1));
    }

    private void encherir(Long idPartie, Long idJoueur, int montant) {
        Partie partie = partieRepository.findById(idPartie).get();

        Enchere enchere = new Enchere();
        enchere.setIdPartie(idPartie);
        enchere.setIdJoueur(idJoueur);
        enchere.setValeur(montant);
        enchere.setManche(partie.getMancheCourante());
        enchereRepository.save(enchere);

        partie.setEnchereMax(montant);
        partie.setPhase(PhaseDeJeu.ENCHERE); // On passe en phase d'enchère
        partieRepository.save(partie);
    }

    private void seRetirer(Long idPartie, Long idJoueur) {
        Partie partie = partieRepository.findById(idPartie).get();

        List<Enchere> encheres = enchereRepository.findByIdPartieAndMancheOrderByDateEnchereAsc(idPartie, partie.getMancheCourante());
        Enchere derniereEnchere = encheres.stream()
                .filter(e -> e.getIdJoueur().equals(idJoueur))
                .reduce((first, second) -> second)
                .orElse(null);

        if (derniereEnchere != null) {
            derniereEnchere.setRetire(true);
            enchereRepository.save(derniereEnchere);
        }

        long nbRetires = enchereRepository.countByIdPartieAndMancheAndRetireTrue(idPartie, partie.getMancheCourante());
        long nbParticipants = participationRepository.countByIdPartie(idPartie);

        if (nbRetires >= nbParticipants - 1) {
            partie.setPhase(PhaseDeJeu.DEFI);
            partieRepository.save(partie);
        }
    }

}
