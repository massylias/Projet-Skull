package fr.info.orleans.projetinfo.Projet_Skull.services;

import fr.info.orleans.projetinfo.Projet_Skull.model.Player;
import fr.info.orleans.projetinfo.Projet_Skull.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PlayerService {

    private final PlayerRepository playerRepository;

    @Autowired
    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    // Récupérer tous les joueurs (utile pour le classement / ladder)
    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    // Trouver un joueur par son ID
    public Optional<Player> getPlayerById(Long id) {
        return playerRepository.findById(id);
    }

    // Trouver un joueur par son nom d'utilisateur
    public Optional<Player> getPlayerByUsername(String username) {
        return playerRepository.findByUsername(username);
    }

    // Enregistrer ou mettre à jour un joueur (utile pour l'inscription)
    public Player savePlayer(Player player) {
        return playerRepository.save(player);
    }

    // Supprimer un joueur
    public void deletePlayer(Long id) {
        playerRepository.deleteById(id);
    }
}