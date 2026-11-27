package fr.info.orleans.projetinfo.ladder.services;

import fr.info.orleans.projetinfo.ladder.dtos.GameHistoryDTO;
import fr.info.orleans.projetinfo.ladder.dtos.GameResultDTO;
import fr.info.orleans.projetinfo.ladder.dtos.PlayerStatsDTO;
import java.util.List;

public interface LadderService {

    //Enregistrement de la partie ici
    void recordGameResult(GameResultDTO resultDTO);

    // Calculer et retourner le classement général
    List<PlayerStatsDTO> getLeaderboard();

    // Récupérer l'historique des parties d'un joueur (par son id en parametre)
    List<GameHistoryDTO> getPlayerHistory(Long playerId);


}
