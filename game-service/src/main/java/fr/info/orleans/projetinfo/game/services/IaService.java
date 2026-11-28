package fr.info.orleans.projetinfo.game.services;

import fr.info.orleans.projetinfo.game.dtos.PlayActionRequest;
import fr.info.orleans.projetinfo.game.dtos.TypeAction;
import fr.info.orleans.projetinfo.game.model.*;
import fr.info.orleans.projetinfo.game.repository.EnchereRepository;
import fr.info.orleans.projetinfo.game.repository.ParticipationRepository;
import fr.info.orleans.projetinfo.game.repository.PartieRepository;
import fr.info.orleans.projetinfo.game.repository.TuileRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Random;

@Service
public class IaService {
    private final PartieRepository partieRepository;
    private final ParticipationRepository participationRepository;
    private final TuileRepository tuileRepository;
    private final Random random = new Random();

    public IaService(PartieRepository partieRepository,
                     ParticipationRepository participationRepository,
                     TuileRepository tuileRepository) {
        this.partieRepository = partieRepository;
        this.participationRepository = participationRepository;
        this.tuileRepository = tuileRepository;
    }

    public PlayActionRequest deciderCoup(Long idPartie, Long idJoueurIa) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        List<Tuile> toutesTuilesIa = tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(idPartie, idJoueurIa);
        List<Tuile> tuilesPoseesIa = toutesTuilesIa.stream().filter(t -> !t.isFaceCachee()).toList();
        List<Tuile> tuilesEnMainIa = toutesTuilesIa.stream().filter(Tuile::isFaceCachee).toList();

        int enchereActuelle = (partie.getEnchereMax() == null ? 0 : partie.getEnchereMax());
        int totalTuilesPosees = compterTotalTuilesPosees(idPartie);

        //  Phase POSE
        if (partie.getPhase() == PhaseDeJeu.POSE) {
            if (tuilesPoseesIa.isEmpty()) {
                return choisirTuileAPoser(tuilesEnMainIa);
            }
            if (tuilesEnMainIa.isEmpty()) {
                return new PlayActionRequest(TypeAction.ENCHERIR, null, enchereActuelle + 1);
            }
            if (random.nextInt(100) < 80) {
                return choisirTuileAPoser(tuilesEnMainIa);
            } else {
                return new PlayActionRequest(TypeAction.ENCHERIR, null, enchereActuelle + 1);
            }
        }

        //  Phase ENCHERE
        if (enchereActuelle >= totalTuilesPosees) {
            return new PlayActionRequest(TypeAction.SE_RETIRER, null, null);
        }

        boolean aPoseCrane = tuilesPoseesIa.stream().anyMatch(t -> t.getType() == TypeTuile.CRANE);
        int chanceSurenchere = aPoseCrane ? 20 : 70;

        if (random.nextInt(100) < chanceSurenchere) {
            return new PlayActionRequest(TypeAction.ENCHERIR, null, enchereActuelle + 1);
        } else {
            return new PlayActionRequest(TypeAction.SE_RETIRER, null, null);
        }
    }

    private PlayActionRequest choisirTuileAPoser(List<Tuile> tuilesEnMain) {
        Tuile choisie = tuilesEnMain.get(random.nextInt(tuilesEnMain.size()));
        return new PlayActionRequest(TypeAction.POSER_TUILE, choisie.getType(), null);
    }

    private int compterTotalTuilesPosees(Long idPartie) {
        return participationRepository.findByIdPartieOrderByPositionAsc(idPartie).stream()
                .mapToInt(p -> (int) tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(idPartie, p.getIdJoueur())
                        .stream().filter(t -> !t.isFaceCachee()).count())
                .sum();
    }

}
