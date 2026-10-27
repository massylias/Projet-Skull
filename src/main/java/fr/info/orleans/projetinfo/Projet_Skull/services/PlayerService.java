package fr.info.orleans.projetinfo.Projet_Skull.services;

import fr.info.orleans.projetinfo.Projet_Skull.dtos.LoginRequest;
import fr.info.orleans.projetinfo.Projet_Skull.dtos.PlayerDTO;
import fr.info.orleans.projetinfo.Projet_Skull.dtos.RegisterRequest;

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

    // Connexion
    PlayerDTO loginPlayer(LoginRequest loginRequest);

    // Bannir un joueur (Admin)
    void banPlayer(Long id);

    // Débannir un joueur (Admin)
    void debanPlayer(Long id);
}