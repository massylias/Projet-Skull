package fr.info.orleans.projetinfo.game.services.impl;

import fr.info.orleans.projetinfo.game.dtos.CreateGameRequest;
import fr.info.orleans.projetinfo.game.dtos.GameDTO;
import fr.info.orleans.projetinfo.game.dtos.JoinGameRequest;
import fr.info.orleans.projetinfo.game.model.*;
import fr.info.orleans.projetinfo.game.repository.EnchereRepository;
import fr.info.orleans.projetinfo.game.repository.ParticipationRepository;
import fr.info.orleans.projetinfo.game.repository.PartieRepository;
import fr.info.orleans.projetinfo.game.repository.TuileRepository;
import fr.info.orleans.projetinfo.game.services.GameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final PartieRepository partieRepository;
    private final ParticipationRepository participationRepository;

    private final TuileRepository tuileRepository;

    private final EnchereRepository enchereRepository;

    public GameServiceImpl(PartieRepository partieRepository, ParticipationRepository participationRepository,  TuileRepository tuileRepository,  EnchereRepository enchereRepository) {
        this.partieRepository = partieRepository;
        this.participationRepository = participationRepository;
        this.tuileRepository = tuileRepository;
        this.enchereRepository = enchereRepository;
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
    // Méthode pour lancer une enchère
    @Override
    @Transactional
    public void bid(Long idPartie, Long idJoueur, int montant) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_COURS) {
            throw new RuntimeException("La partie n'est pas en cours");
        }

        //  Vérifier l'enchère
        int enchereActuelle = (partie.getEnchereMax() == null ? 0 : partie.getEnchereMax());
        if (montant <= enchereActuelle) {
            throw new RuntimeException("L'enchère doit être supérieure à l'enchère actuelle");
        }

        // Enregistrer l'enchère
        Enchere enchere = new Enchere();
        enchere.setIdPartie(idPartie);
        enchere.setIdJoueur(idJoueur);
        enchere.setValeur(montant);
        enchere.setManche(partie.getMancheCourante());
        enchereRepository.save(enchere);

        // Mettre à jour la partie
        partie.setEnchereMax(montant);
        

        //  Éviter le null
        int courant = (partie.getJoueurCourant() == null ? 0 : partie.getJoueurCourant());
        partie.setJoueurCourant(courant + 1);

        //  Passer en phase ENCHERE
        partie.setPhase(PhaseDeJeu.ENCHERE);

        partieRepository.save(partie);


    }

    // Méthode pour se retirer (lors d'une enchère)
    @Override
    @Transactional
    public void seRetirer(Long idPartie, Long idJoueur) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_COURS) {
            throw new RuntimeException("La partie n'est pas en cours");
        }

        //  Trouver la dernière enchère du joueur pour la manche en cours
        List<Enchere> encheres = enchereRepository.findByIdPartieAndMancheOrderByDateEnchereAsc(idPartie, partie.getMancheCourante());

        Enchere derniereEnchere = encheres.stream()
                .filter(e -> e.getIdJoueur().equals(idJoueur))
                .reduce((first, second) -> second) // Prend la dernière
                .orElse(null);

        if (derniereEnchere == null) {
            throw new RuntimeException("Vous n'avez pas encore enchéri");
        }

        //  Marquer l'enchère comme retirée
        derniereEnchere.setRetire(true);
        enchereRepository.save(derniereEnchere);

        //  Vérifier s'il ne reste qu'un seul joueur non retiré
        long nbRetires = enchereRepository.countByIdPartieAndMancheAndRetireTrue(idPartie, partie.getMancheCourante());
        long nbParticipants = participationRepository.countByIdPartie(idPartie);

        if (nbRetires >= nbParticipants - 1) {
            // Un seul joueur reste, on passe en phase de DEFI
            partie.setPhase(PhaseDeJeu.DEFI);
            partieRepository.save(partie);
        }
    }
    // Résoudre un défi
    @Override
    @Transactional
    public void resoudreDefi(Long idPartie, Long idJoueur, int nombreTuilesARetourner) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_COURS) {
            throw new RuntimeException("La partie n'est pas en cours");
        }

        // Récupérer toutes les tuiles posées (faceCachee = false) pour cette partie
        List<Tuile> tuilesPosees = tuileRepository.findByIdPartie(idPartie)
                .stream().filter(t -> !t.isFaceCachee()).toList();

        if (tuilesPosees.size() < nombreTuilesARetourner) {
            throw new RuntimeException("Il n'y a pas assez de tuiles posées pour ce défi");
        }

        // Le joueur commence par sa propre pile
        List<Tuile> aRetourner = new ArrayList<>(
                tuilesPosees.stream().filter(t -> t.getIdJoueur().equals(idJoueur)).toList()
        );

        // Compléter avec les autres joueurs si besoin
        if (aRetourner.size() < nombreTuilesARetourner) {
            List<Tuile> autres = tuilesPosees.stream()
                    .filter(t -> !t.getIdJoueur().equals(idJoueur))
                    .limit(nombreTuilesARetourner - aRetourner.size())
                    .toList();
            aRetourner.addAll(autres);
        }

        // Vérifier s'il y a un crâne parmi les tuiles retournées
        boolean craneTrouve = aRetourner.stream()
                .anyMatch(t -> t.getType() == TypeTuile.CRANE);

        Participation participation = participationRepository.findByIdPartieAndIdJoueur(idPartie, idJoueur)
                .orElseThrow(() -> new RuntimeException("Participation introuvable"));

        if (craneTrouve) {
            // Le joueur perd un disque
            participation.setTuilesRestantes(participation.getTuilesRestantes() - 1);
            participationRepository.save(participation);
        } else {
            // Le joueur gagne un point
            participation.setScore(participation.getScore() + 1);
            participationRepository.save(participation);
        }
        //   Vérification de Victoire ou d'Éliminations
        verifierFinDePartie(partie);

        // Réinitialiser la manche (remettre les tuiles en main, passer à la manche suivante)
        if (partie.getStatut() == StatutPartie.EN_COURS) {
            resetManche(partie);
        }

    }
    private void verifierFinDePartie(Partie partie) {
        List<Participation> participations = participationRepository.findByIdPartieOrderByPositionAsc(partie.getId());

        long nbJoueursActifs = 0;
        for (Participation p : participations) {
            // Victoire : 2 points
            if (p.getScore() >= 2) {
                partie.setStatut(StatutPartie.TERMINEE);
                partie.setDateFin(java.time.LocalDateTime.now());
                partieRepository.save(partie);
                System.out.println(">>> PARTIE TERMINÉE : Le joueur " + p.getIdJoueur() + " a gagné !");
                return;
            }
            // Élimination : plus de tuiles
            if (p.getTuilesRestantes() <= 0) {
                p.setaQuitte(true);
                participationRepository.save(p);
            } else {
                nbJoueursActifs++;
            }
        }

        // Si un seul joueur actif, il gagne par forfait
        if (nbJoueursActifs <= 1) {
            partie.setStatut(StatutPartie.TERMINEE);
            partie.setDateFin(java.time.LocalDateTime.now());
            partieRepository.save(partie);
            System.out.println(">>> PARTIE TERMINÉE : Il ne reste qu'un joueur.");
        }
    }


    private void resetManche(Partie partie) {
        // Remettre toutes les tuiles en main
        List<Tuile> tuiles = tuileRepository.findByIdPartie(partie.getId());
        for (Tuile t : tuiles) {
            t.setFaceCachee(true);
            tuileRepository.save(t);
        }

        // Incrémenter la manche
        partie.setMancheCourante(partie.getMancheCourante() + 1);
        partie.setPhase(PhaseDeJeu.POSE);
        partie.setEnchereMax(null);
        partie.setJoueurCourant(0);
        partieRepository.save(partie);
    }

}
