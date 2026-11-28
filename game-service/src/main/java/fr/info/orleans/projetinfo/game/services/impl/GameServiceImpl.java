package fr.info.orleans.projetinfo.game.services.impl;

import fr.info.orleans.projetinfo.game.dtos.CreateGameRequest;
import fr.info.orleans.projetinfo.game.dtos.GameDTO;
import fr.info.orleans.projetinfo.game.dtos.JoinGameRequest;
import fr.info.orleans.projetinfo.game.model.*;
import fr.info.orleans.projetinfo.game.repository.ParticipationRepository;
import fr.info.orleans.projetinfo.game.repository.PartieRepository;
import fr.info.orleans.projetinfo.game.repository.TuileRepository;
import fr.info.orleans.projetinfo.game.services.GameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final PartieRepository partieRepository;
    private final ParticipationRepository participationRepository;

    private final TuileRepository tuileRepository;

    public GameServiceImpl(PartieRepository partieRepository, ParticipationRepository participationRepository,  TuileRepository tuileRepository) {
        this.partieRepository = partieRepository;
        this.participationRepository = participationRepository;
        this.tuileRepository = tuileRepository;
    }

    @Override
    @Transactional
    public GameDTO createGame(CreateGameRequest request, Long idCreateur) {

        //  Créer la partie
        Partie partie = new Partie();
        partie.setNbJoueurs(request.getNbJoueurs());
        partie.setNbIa(request.getNbIa());
        partie.setIdCreateur(idCreateur);
        partie.setStatut(StatutPartie.EN_ATTENTE);
        partieRepository.save(partie);

        //  Ajouter le créateur comme premier participant (position 0)
        Participation createur = new Participation();
        createur.setIdPartie(partie.getId());
        createur.setIdJoueur(idCreateur);
        createur.setEstIa(false);
        createur.setPosition(0);
        participationRepository.save(createur);

        //  Retourner le DTO
        return GameDTO.from(partie, 1); // 1 participant pour l'instant
    }

    @Override
    public List<GameDTO> getGames() {

        // On récupère les parties EN_ATTENTE, triées par date de création
        List<Partie> parties = partieRepository.findByStatutOrderByDateCreationDesc(StatutPartie.EN_ATTENTE);

        return parties.stream().map(partie -> {
            long nbParticipants = participationRepository.countByIdPartie(partie.getId());
            return GameDTO.from(partie, (int) nbParticipants);
        }).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void joinGame(Long idPartie, Long idJoueur, JoinGameRequest request) {

        //  Vérifier que la partie existe et est en attente
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_ATTENTE) {
            throw new RuntimeException("La partie a déjà commencé ou est terminée");
        }

        //  Vérifier que le joueur n'est pas déjà dans la partie
        if (participationRepository.existsByIdPartieAndIdJoueur(idPartie, idJoueur)) {
            throw new RuntimeException("Vous avez déjà rejoint cette partie");
        }

        //  Vérifier s'il reste de la place
        long nbParticipants = participationRepository.countByIdPartie(idPartie);
        if (nbParticipants >= partie.getNbJoueurs()) {
            throw new RuntimeException("La partie est complète");
        }

        //  Ajouter le joueur
        Participation participation = new Participation();
        participation.setIdPartie(idPartie);
        participation.setIdJoueur(idJoueur);
        participation.setEstIa(false);
        participation.setPosition((int) nbParticipants); // Position suivante
        participationRepository.save(participation);

        //  Si la partie est pleine, on pourrait la lancer (optionnel pour l'instant)
        if (nbParticipants + 1 == partie.getNbJoueurs()) {
            partie.setStatut(StatutPartie.EN_COURS);
            partieRepository.save(partie);
        }
    }

    @Override
    @Transactional
    public void leaveGame(Long idPartie, Long idJoueur) {
        //  Vérifier que la partie existe
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        //  Trouver la participation du joueur
        Participation participation = participationRepository.findByIdPartieAndIdJoueur(idPartie, idJoueur)
                .orElseThrow(() -> new RuntimeException("Vous ne participez pas à cette partie"));

        //  Si la partie est en cours, on remplace par une IA (ou on marque comme quitté)
        if (partie.getStatut() == StatutPartie.EN_COURS) {
            participation.setaQuitte(true);
            participation.setEstIa(true); // On transforme en IA
            participationRepository.save(participation);
        } else {
            // Si la partie n'a pas commencé, on supprime simplement la participation
            participationRepository.delete(participation);
        }
    }

    // Méthode pour démarrer la partie
    @Override
    @Transactional
    public void startGame(Long idPartie) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_ATTENTE) {
            throw new RuntimeException("La partie a déjà commencé ou est terminée");
        }

        long nbParticipantsHumains = participationRepository.countByIdPartie(idPartie);
        if (nbParticipantsHumains + partie.getNbIa() < 3) {
            throw new RuntimeException("Il faut au moins 3 joueurs (humains + IA)");
        }

        //  Ajouter les IA manquantes
        long nbIaActuelles = participationRepository.countByIdPartieAndEstIaTrue(idPartie);
        long nbIaAAjouter = partie.getNbIa() - nbIaActuelles;

        for (int i = 0; i < nbIaAAjouter; i++) {
            Participation ia = new Participation();
            ia.setIdPartie(idPartie);
            ia.setIdJoueur(-1L - i); // ID négatif pour les IA
            ia.setEstIa(true);
            ia.setPosition((int) nbParticipantsHumains + i);
            participationRepository.save(ia);
        }

        //  Récupérer tous les participants (humains + IA)
        List<Participation> participations = participationRepository.findByIdPartieOrderByPositionAsc(idPartie);

        //  Distribuer les tuiles à tous les participants
        for (Participation p : participations) {
            tuileRepository.save(new Tuile(idPartie, p.getIdJoueur(), TypeTuile.FLEUR, 0));
            tuileRepository.save(new Tuile(idPartie, p.getIdJoueur(), TypeTuile.FLEUR, 1));
            tuileRepository.save(new Tuile(idPartie, p.getIdJoueur(), TypeTuile.FLEUR, 2));
            tuileRepository.save(new Tuile(idPartie, p.getIdJoueur(), TypeTuile.CRANE, 3));
        }

        //  Mettre à jour la partie
        partie.setStatut(StatutPartie.EN_COURS);
        partie.setPhase(PhaseDeJeu.POSE);
        partie.setMancheCourante(1);
        partie.setJoueurCourant(0);
        partieRepository.save(partie);
    }

    @Override
    @Transactional
    public void playTuile(Long idPartie, Long idJoueur) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_COURS) {
            throw new RuntimeException("La partie n'est pas en cours");
        }

        List<Tuile> tuiles = tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(idPartie, idJoueur);

        Tuile tuileAPoser = tuiles.stream()
                .filter(Tuile::isFaceCachee)
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Vous n'avez plus de tuiles à poser"));

        tuileAPoser.setFaceCachee(false);
        tuileRepository.save(tuileAPoser);

        Participation participation = participationRepository.findByIdPartieAndIdJoueur(idPartie, idJoueur)
                .orElseThrow(() -> new RuntimeException("Participation introuvable"));

        participation.setTuilesRestantes(participation.getTuilesRestantes() - 1);
        participationRepository.save(participation);
    }
}
