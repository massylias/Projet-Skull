package fr.info.orleans.projetinfo.game.services.impl;

import fr.info.orleans.projetinfo.game.dtos.*;
import fr.info.orleans.projetinfo.game.model.*;
import fr.info.orleans.projetinfo.game.repository.*;
import fr.info.orleans.projetinfo.game.services.GameService;
import fr.info.orleans.projetinfo.game.services.IaService;
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

    private final SignalementRepository signalementRepository;

    private final IaService iaService;

    public GameServiceImpl(PartieRepository partieRepository, ParticipationRepository participationRepository,  TuileRepository tuileRepository,  EnchereRepository enchereRepository, SignalementRepository signalementRepository,
                           IaService iaService) {
        this.partieRepository = partieRepository;
        this.participationRepository = participationRepository;
        this.tuileRepository = tuileRepository;
        this.enchereRepository = enchereRepository;
        this.signalementRepository = signalementRepository;
        this.iaService = iaService;
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
    // Méthode pour l'état de la partie
    @Override
    public GameStateDTO getGameState(Long idPartie, Long idJoueur) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        List<Participation> participations = participationRepository.findByIdPartieOrderByPositionAsc(idPartie);
        List<ParticipantDTO> participantDTOs = participations.stream().map(p -> {
            int nbPosees = (int) tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(idPartie, p.getIdJoueur())
                    .stream().filter(t -> !t.isFaceCachee()).count();
            return new ParticipantDTO(
                    p.getPosition(),
                    p.getIdJoueur(),
                    p.isEstIa(),
                    p.isaQuitte(),
                    p.getScore(),
                    p.getTuilesRestantes(),
                    nbPosees
            );
        }).toList();

        List<TypeTuile> mesTuilesPosees = (idJoueur != null)
                ? tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(idPartie, idJoueur)
                .stream()
                .filter(t -> !t.isFaceCachee())
                .map(Tuile::getType)
                .toList()
                : List.of();

        return new GameStateDTO(
                partie.getId(),
                partie.getStatut(),
                partie.getPhase(),
                partie.getMancheCourante(),
                partie.getJoueurCourant(),
                partie.getEnchereMax(),
                participantDTOs,
                mesTuilesPosees
        );
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

        // Si le joueur en position 0 est une IA, elle joue immédiatement
        if (!participations.isEmpty() && participations.get(0).isEstIa()) {
            PlayActionRequest coupIa = iaService.deciderCoup(idPartie, participations.get(0).getIdJoueur());
            executerPose(partie, participations.get(0).getIdJoueur(), coupIa.getTypeTuile());
            passerAuJoueurSuivant(partie);
        }
    }

    private void verifierTourJoueur(Partie partie, Long idJoueur) {
        List<Participation> participants = participationRepository.findByIdPartieOrderByPositionAsc(partie.getId());
        int indexCourant = (partie.getJoueurCourant() == null ? 0 : partie.getJoueurCourant());
        if (indexCourant >= participants.size() || !participants.get(indexCourant).getIdJoueur().equals(idJoueur)) {
            throw new RuntimeException("Ce n'est pas votre tour");
        }
    }


    @Override
    @Transactional
    public void playTuile(Long idPartie, Long idJoueur) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_COURS || partie.getPhase() != PhaseDeJeu.POSE) {
            throw new RuntimeException("Impossible de poser une tuile actuellement");
        }
        verifierTourJoueur(partie, idJoueur);

        executerPose(partie, idJoueur, null);
        passerAuJoueurSuivant(partie);
    }

    // Méthode pour lancer une enchère
    @Override
    @Transactional
    public void bid(Long idPartie, Long idJoueur, int montant) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_COURS || partie.getPhase() == PhaseDeJeu.DEFI) {
            throw new RuntimeException("Impossible d'enchérir actuellement");
        }
        verifierTourJoueur(partie, idJoueur);

        executerEnchere(partie, idJoueur, montant);
        if (partie.getPhase() == PhaseDeJeu.ENCHERE) {
            passerAuJoueurSuivant(partie);
        }
    }

    // Méthode pour se retirer (lors d'une enchère)
    @Override
    @Transactional
    public void seRetirer(Long idPartie, Long idJoueur) {
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() != StatutPartie.EN_COURS || partie.getPhase() != PhaseDeJeu.ENCHERE) {
            throw new RuntimeException("Impossible de se retirer actuellement");
        }
        verifierTourJoueur(partie, idJoueur);


        executerRetrait(partie, idJoueur);
        if (partie.getPhase() == PhaseDeJeu.ENCHERE) {
            passerAuJoueurSuivant(partie);
        }
    }
    // Méthodes internes partagées entre Humains et IA

    private void executerPose(Partie partie, Long idJoueur, TypeTuile typeSouhaite) {
        List<Tuile> tuiles = tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(partie.getId(), idJoueur);

        Tuile tuileAPoser = tuiles.stream()
                .filter(Tuile::isFaceCachee)
                .filter(t -> typeSouhaite == null || t.getType() == typeSouhaite)
                .findFirst()
                .orElseGet(() -> tuiles.stream().filter(Tuile::isFaceCachee).findFirst()
                        .orElseThrow(() -> new RuntimeException("Vous n'avez plus de tuiles à poser")));

        tuileAPoser.setFaceCachee(false);
        tuileRepository.save(tuileAPoser);
    }

    private void executerEnchere(Partie partie, Long idJoueur, int montant) {
        boolean aPoseTuile = tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(partie.getId(), idJoueur)
                .stream().anyMatch(t -> !t.isFaceCachee());
        if (!aPoseTuile) {
            throw new RuntimeException("Vous devez poser au moins une tuile avant d'enchérir");
        }

        int enchereActuelle = (partie.getEnchereMax() == null ? 0 : partie.getEnchereMax());
        long totalTuilesPosees = tuileRepository.findByIdPartie(partie.getId()).stream().filter(t -> !t.isFaceCachee()).count();

        if (montant <= enchereActuelle) {
            throw new RuntimeException("L'enchère doit être supérieure à l'enchère actuelle (" + enchereActuelle + ")");
        }
        if (montant > totalTuilesPosees) {
            throw new RuntimeException("L'enchère ne peut pas dépasser le nombre de tuiles posées (" + totalTuilesPosees + ")");
        }

        Enchere enchere = new Enchere();
        enchere.setIdPartie(partie.getId());
        enchere.setIdJoueur(idJoueur);
        enchere.setValeur(montant);
        enchere.setManche(partie.getMancheCourante());
        enchere.setRetire(false);
        enchereRepository.save(enchere);

        partie.setEnchereMax(montant);
        partie.setPhase(PhaseDeJeu.ENCHERE);
        partieRepository.save(partie);

        // Si le plafond maximum est atteint, on passe directement au défi
        if (montant == totalTuilesPosees) {
            partie.setPhase(PhaseDeJeu.DEFI);
            partieRepository.save(partie);
            gererDefiSiIa(partie, idJoueur);
        }
    }

    private void executerRetrait(Partie partie, Long idJoueur) {
        List<Enchere> encheres = enchereRepository.findByIdPartieAndMancheOrderByDateEnchereAsc(partie.getId(), partie.getMancheCourante());
        Enchere derniereEnchere = encheres.stream()
                .filter(e -> e.getIdJoueur().equals(idJoueur))
                .reduce((first, second) -> second)
                .orElse(null);

        if (derniereEnchere != null) {
            derniereEnchere.setRetire(true);
            enchereRepository.save(derniereEnchere);
        } else {
            Enchere retrait = new Enchere();
            retrait.setIdPartie(partie.getId());
            retrait.setIdJoueur(idJoueur);
            retrait.setValeur(0);
            retrait.setManche(partie.getMancheCourante());
            retrait.setRetire(true);
            enchereRepository.save(retrait);
        }

        long nbRetires = enchereRepository.countByIdPartieAndMancheAndRetireTrue(partie.getId(), partie.getMancheCourante());
        List<Participation> actifs = participationRepository.findByIdPartieOrderByPositionAsc(partie.getId())
                .stream().filter(p -> p.getTuilesRestantes() > 0).toList();

        if (nbRetires >= actifs.size() - 1) {
            partie.setPhase(PhaseDeJeu.DEFI);
            partieRepository.save(partie);

            // Retrouver le gagnant de l'enchère
            Enchere gagnante = enchereRepository.findByIdPartieAndMancheOrderByDateEnchereAsc(partie.getId(), partie.getMancheCourante())
                    .stream()
                    .filter(e -> !e.isRetire() && e.getValeur() > 0)
                    .reduce((first, second) -> second)
                    .orElse(null);

            if (gagnante != null) {
                gererDefiSiIa(partie, gagnante.getIdJoueur());
            }
        }
    }

    private void passerAuJoueurSuivant(Partie partie) {
        while (partie.getStatut() == StatutPartie.EN_COURS && partie.getPhase() != PhaseDeJeu.DEFI) {
            List<Participation> participants = participationRepository.findByIdPartieOrderByPositionAsc(partie.getId());
            if (participants.isEmpty()) return;

            int nbTotal = participants.size();
            int indexActuel = (partie.getJoueurCourant() == null ? 0 : partie.getJoueurCourant());
            Participation prochainJoueur = null;

            for (int i = 1; i <= nbTotal; i++) {
                int prochainIndex = (indexActuel + i) % nbTotal;
                Participation candidat = participants.get(prochainIndex);

                if (candidat.getTuilesRestantes() <= 0) continue;

                if (partie.getPhase() == PhaseDeJeu.ENCHERE) {
                    boolean dejaRetire = enchereRepository
                            .findByIdPartieAndMancheOrderByDateEnchereAsc(partie.getId(), partie.getMancheCourante())
                            .stream()
                            .anyMatch(e -> e.getIdJoueur().equals(candidat.getIdJoueur()) && e.isRetire());
                    if (dejaRetire) continue;
                }

                partie.setJoueurCourant(prochainIndex);
                partieRepository.save(partie);
                prochainJoueur = candidat;
                break;
            }

            if (prochainJoueur == null || !prochainJoueur.isEstIa()) {
                break;
            }

            PlayActionRequest coupIa = iaService.deciderCoup(partie.getId(), prochainJoueur.getIdJoueur());
            switch (coupIa.getAction()) {
                case POSER_TUILE -> executerPose(partie, prochainJoueur.getIdJoueur(), coupIa.getTypeTuile());
                case ENCHERIR -> executerEnchere(partie, prochainJoueur.getIdJoueur(), coupIa.getValeur());
                case SE_RETIRER -> executerRetrait(partie, prochainJoueur.getIdJoueur());
            }
        }
    }

    private void gererDefiSiIa(Partie partie, Long idVainqueurEnchere) {
        participationRepository.findByIdPartieAndIdJoueur(partie.getId(), idVainqueurEnchere).ifPresent(p -> {
            if (p.isEstIa()) {
                resoudreDefi(partie.getId(), idVainqueurEnchere, partie.getEnchereMax());
            }
        });
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
            List<Tuile> tuilesJoueur = tuileRepository.findByIdPartieAndIdJoueurOrderByPositionAsc(idPartie, idJoueur);
            if (!tuilesJoueur.isEmpty()) {
                tuileRepository.delete(tuilesJoueur.get(tuilesJoueur.size() - 1));
            }
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

        List<Participation> participants = participationRepository.findByIdPartieOrderByPositionAsc(partie.getId());
        if (!participants.isEmpty()) {
            Participation premier = participants.get(0);
            if (premier.getTuilesRestantes() <= 0) {
                passerAuJoueurSuivant(partie);
            } else if (premier.isEstIa()) {
                PlayActionRequest coupIa = iaService.deciderCoup(partie.getId(), premier.getIdJoueur());
                executerPose(partie, premier.getIdJoueur(), coupIa.getTypeTuile());
                passerAuJoueurSuivant(partie);
            }
        }
    }

    // Méthode pour signaler un joueur
    @Override
    @Transactional
    public void signalerJoueur(Long idPartie, Long idJoueurSignaleur, Long idJoueurSignale, String raison) {
        //  Vérifier que la partie existe et a démarré (EN_COURS ou TERMINEE pour le signalement à posteriori)
        Partie partie = partieRepository.findById(idPartie)
                .orElseThrow(() -> new RuntimeException("Partie introuvable"));

        if (partie.getStatut() == StatutPartie.EN_ATTENTE) {
            throw new RuntimeException("Impossible de signaler un joueur avant le début de la partie");
        }

        //  Empêcher un joueur de se signaler lui-même
        if (idJoueurSignaleur.equals(idJoueurSignale)) {
            throw new RuntimeException("Vous ne pouvez pas vous signaler vous-même");
        }

        //  Vérifier que les deux joueurs ont bien participé à cette partie
        if (!participationRepository.existsByIdPartieAndIdJoueur(idPartie, idJoueurSignaleur)) {
            throw new RuntimeException("Vous ne participez pas à cette partie");
        }
        if (!participationRepository.existsByIdPartieAndIdJoueur(idPartie, idJoueurSignale)) {
            throw new RuntimeException("Le joueur signalé n'a pas participé à cette partie");
        }

        //  Vérifier qu'un signalement identique n'existe pas déjà
        if (signalementRepository.existsByIdPartieAndIdJoueurSignaleAndIdJoueurSignaleur(
                idPartie, idJoueurSignale, idJoueurSignaleur)) {
            throw new RuntimeException("Vous avez déjà signalé ce joueur pour cette partie");
        }

        //  Créer le signalement
        Signalement signalement = new Signalement();
        signalement.setIdPartie(idPartie);
        signalement.setIdJoueurSignaleur(idJoueurSignaleur);
        signalement.setIdJoueurSignale(idJoueurSignale);
        signalement.setRaison(raison);
        signalement.setTraite(false);

        signalementRepository.save(signalement);
    }

}
