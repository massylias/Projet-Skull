package fr.info.orleans.projetinfo.Projet_Skull.services;

import fr.info.orleans.projetinfo.Projet_Skull.model.Player;

import java.util.List;
import java.util.Optional;

public interface PlayerService {

    List<Player> getAllPlayers();

    Optional<Player> getPlayerById(Long id);

    Optional<Player> getPlayerByEmail(String email);

    Player savePlayer(Player player);

    void deletePlayer(Long id);

    Player banPlayer(Long id);
}