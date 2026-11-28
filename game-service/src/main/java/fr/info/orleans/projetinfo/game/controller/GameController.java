package fr.info.orleans.projetinfo.game.controller;

import fr.info.orleans.projetinfo.game.dtos.CreateGameRequest;
import fr.info.orleans.projetinfo.game.dtos.GameDTO;
import fr.info.orleans.projetinfo.game.dtos.GameStateDTO;
import fr.info.orleans.projetinfo.game.dtos.JoinGameRequest;
import fr.info.orleans.projetinfo.game.model.TypeTuile;
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

    // Voir l'état d'une partie
    @GetMapping("/{id}")
    public ResponseEntity<GameStateDTO> getGameState(@PathVariable Long id,
                                                     @RequestParam(required = false) Long idJoueur) {
        return ResponseEntity.ok(gameService.getGameState(id, idJoueur));
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

    // Démarrer la partie
    @PostMapping("/{id}/start")
    public ResponseEntity<Void> startGame(@PathVariable Long id) {
        gameService.startGame(id);
        return ResponseEntity.ok().build();
    }

    // Poser une tuile
    @PostMapping("/{id}/play-tuile")
    public ResponseEntity<Void> playTuile(@PathVariable Long id, @RequestParam Long idJoueur,
                                          @RequestParam(required = false) TypeTuile typeTuile) {
        gameService.playTuile(id, idJoueur, typeTuile);
        return ResponseEntity.ok().build();
    }

    // Lancer une enchère
    @PostMapping("/{id}/bid")
    public ResponseEntity<Void> bid(@PathVariable Long id,
                                    @RequestParam Long idJoueur,
                                    @RequestParam int montant) {
        gameService.bid(id, idJoueur, montant);
        return ResponseEntity.ok().build();
    }

    // Se retirer de l'enchère
    @PostMapping("/{id}/se-retirer")
    public ResponseEntity<Void> seRetirer(@PathVariable Long id, @RequestParam Long idJoueur) {
        gameService.seRetirer(id, idJoueur);
        return ResponseEntity.ok().build();
    }
    // Résoudre un défi
    @PostMapping("/{id}/defi")
    public ResponseEntity<Void> resoudreDefi(@PathVariable Long id,
                                             @RequestParam Long idJoueur,
                                             @RequestParam int nombreTuiles) {
        gameService.resoudreDefi(id, idJoueur, nombreTuiles);
        return ResponseEntity.ok().build();
    }

    // Signaler un Joueur
    @PostMapping("/{id}/report")
    public ResponseEntity<Void> signalerJoueur(@PathVariable Long id,
                                               @RequestParam Long idJoueurSignaleur,
                                               @RequestParam Long idJoueurSignale,
                                               @RequestParam String raison) {
        gameService.signalerJoueur(id, idJoueurSignaleur, idJoueurSignale, raison);
        return ResponseEntity.ok().build();
    }
}

