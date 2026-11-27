package fr.info.orleans.projetinfo.game.services.impl;

import fr.info.orleans.projetinfo.game.dtos.CreateGameRequest;
import fr.info.orleans.projetinfo.game.dtos.GameDTO;
import fr.info.orleans.projetinfo.game.dtos.JoinGameRequest;
import fr.info.orleans.projetinfo.game.model.Participation;
import fr.info.orleans.projetinfo.game.model.Partie;
import fr.info.orleans.projetinfo.game.model.StatutPartie;
import fr.info.orleans.projetinfo.game.repository.ParticipationRepository;
import fr.info.orleans.projetinfo.game.repository.PartieRepository;
import fr.info.orleans.projetinfo.game.services.GameService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GameServiceImpl implements GameService {

    private final PartieRepository partieRepository;
    private final ParticipationRepository participationRepository;

    public GameServiceImpl(PartieRepository partieRepository, ParticipationRepository participationRepository) {
        this.partieRepository = partieRepository;
        this.participationRepository = participationRepository;
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



}
