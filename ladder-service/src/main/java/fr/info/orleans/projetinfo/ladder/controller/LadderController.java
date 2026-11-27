package fr.info.orleans.projetinfo.ladder.controller;

import fr.info.orleans.projetinfo.ladder.dtos.GameHistoryDTO;
import fr.info.orleans.projetinfo.ladder.dtos.GameResultDTO;
import fr.info.orleans.projetinfo.ladder.dtos.PlayerStatsDTO;
import fr.info.orleans.projetinfo.ladder.services.LadderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ladder")
public class LadderController {

    private final LadderService ladderService;

    public LadderController(LadderService ladderService) {
        this.ladderService = ladderService;
    }

    //  Enregistrer un résultat de partie
    @PostMapping("/record")
    public ResponseEntity<Void> recordGameResult(@RequestBody GameResultDTO resultDTO) {
        ladderService.recordGameResult(resultDTO);
        return ResponseEntity.ok().build();
    }

    // Obtenir le classement général
    @GetMapping
    public ResponseEntity<List<PlayerStatsDTO>> getLeaderboard() {
        List<PlayerStatsDTO> leaderboard = ladderService.getLeaderboard();
        return ResponseEntity.ok(leaderboard);
    }

    // Obtenir l'historique d'un joueur spécifique
    @GetMapping("/history/{playerId}")
    public ResponseEntity<List<GameHistoryDTO>> getPlayerHistory(@PathVariable Long playerId) {
        List<GameHistoryDTO> history = ladderService.getPlayerHistory(playerId);
        return ResponseEntity.ok(history);
    }
}