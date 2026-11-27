package fr.info.orleans.projetinfo.game.controller;

import fr.info.orleans.projetinfo.game.dtos.CreateGameRequest;
import fr.info.orleans.projetinfo.game.dtos.GameDTO;
import fr.info.orleans.projetinfo.game.dtos.JoinGameRequest;
import fr.info.orleans.projetinfo.game.services.GameService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/games")
public class GameController {
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    // Créer une partie
    @PostMapping
    public ResponseEntity<GameDTO> createGame(@Valid @RequestBody CreateGameRequest request,
                                              @RequestParam Long idCreateur) {
        return ResponseEntity.ok(gameService.createGame(request, idCreateur));
    }

    // Lister les parties ouvertes
    @GetMapping
    public ResponseEntity<List<GameDTO>> getGames() {
        return ResponseEntity.ok(gameService.getGames());
    }

    // Rejoindre une partie
    @PostMapping("/{id}/join")
    public ResponseEntity<Void> joinGame(@PathVariable Long id,
                                         @RequestBody JoinGameRequest request,
                                         @RequestParam Long idJoueur) {
        gameService.joinGame(id, idJoueur, request);
        return ResponseEntity.ok().build();
    }

    // Quitter une partie
    @PostMapping("/{id}/leave")
    public ResponseEntity<Void> leaveGame(@PathVariable Long id,
                                          @RequestParam Long idJoueur) {
        gameService.leaveGame(id, idJoueur);
        return ResponseEntity.ok().build();
    }

}
