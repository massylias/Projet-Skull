package fr.info.orleans.projetinfo.player.services;

import fr.info.orleans.projetinfo.player.dtos.LoginRequest;
import fr.info.orleans.projetinfo.player.dtos.LoginResponse;
import fr.info.orleans.projetinfo.player.dtos.PlayerDTO;
import fr.info.orleans.projetinfo.player.dtos.RegisterRequest;

import java.util.List;

public interface PlayerService {

    // Lister tous les joueurs
    List<PlayerDTO> getAllPlayers();

    // Récupérer un joueur par ID
    PlayerDTO getPlayerById(Long id);

    // Récupérer un joueur par son email
    PlayerDTO getPlayerByEmail(String email);

    // Inscription
    PlayerDTO savePlayer(RegisterRequest registerRequest);

    /*
    [(Avant la Sécurisation et JWT)]
    // Connexion
    //PlayerDTO loginPlayer(LoginRequest loginRequest);
    */

    // Connexion [(Avec Sécurisation et JWT)]
    LoginResponse loginPlayer(LoginRequest loginRequest);

    // Bannir un joueur (Admin)
    void banPlayer(Long id);

    // Débannir un joueur (Admin)
    void debanPlayer(Long id);
}