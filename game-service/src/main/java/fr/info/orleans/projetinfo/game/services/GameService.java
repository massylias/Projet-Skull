package fr.info.orleans.projetinfo.game.services;

import fr.info.orleans.projetinfo.game.dtos.CreateGameRequest;
import fr.info.orleans.projetinfo.game.dtos.GameDTO;
import fr.info.orleans.projetinfo.game.dtos.GameStateDTO;
import fr.info.orleans.projetinfo.game.dtos.JoinGameRequest;
import fr.info.orleans.projetinfo.game.model.TypeTuile;

import java.util.List;

public interface GameService {

    //Créer une partie
    GameDTO createGame(CreateGameRequest request, Long idCreateur);

    // Lister les parties ouvertes
    List<GameDTO> getGames();

    //Rejoindre une partie
    void joinGame(Long idPartie, Long idJoueur, JoinGameRequest request);

    //Quitter une partie
    void leaveGame(Long idPartie, Long idJoueur);

    // État de la partie
    GameStateDTO getGameState(Long idPartie, Long idJoueur);

    // Commencer la partie
    void startGame(Long idPartie);

    // Poser une tuile
    void playTuile(Long idPartie, Long idJoueur, TypeTuile  typeTuile);

    // Lancer une enchère
    void bid(Long idPartie, Long idJoueur, int montant);

    // Se retirer lors d'une enchère
    void seRetirer(Long idPartie, Long idJoueur);

    // Résoudre un défi
    void resoudreDefi(Long idPartie, Long idJoueur, int nombreTuilesARetourner);

    // Signaler un Joueur
    void signalerJoueur(Long idPartie, Long idJoueurSignaleur, Long idJoueurSignale, String raison);
}
