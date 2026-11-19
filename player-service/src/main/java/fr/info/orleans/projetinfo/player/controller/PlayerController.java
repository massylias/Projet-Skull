package fr.info.orleans.projetinfo.player.controller;
import fr.info.orleans.projetinfo.player.dtos.LoginRequest;
import fr.info.orleans.projetinfo.player.dtos.LoginResponse;
import fr.info.orleans.projetinfo.player.dtos.PlayerDTO;
import fr.info.orleans.projetinfo.player.dtos.RegisterRequest;
import fr.info.orleans.projetinfo.player.services.PlayerService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;


    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Récupérer la liste de tous les joueurs
    @GetMapping
    public ResponseEntity<List<PlayerDTO>> getAllPlayers() {
        return ResponseEntity.ok(playerService.getAllPlayers());
    }

    // Récupérer un joueur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<PlayerDTO> getPlayerById(@PathVariable Long id) {
        return ResponseEntity.ok(playerService.getPlayerById(id));
    }


    // Inscription d'un joueur (/api/players/register)
    @PostMapping("/register")
    public ResponseEntity<PlayerDTO> registerPlayer(@Valid @RequestBody RegisterRequest registerRequest) {
        PlayerDTO player = playerService.savePlayer(registerRequest);
        return new ResponseEntity<>(player, HttpStatus.CREATED);
    }
    /*
     [Endpoint Avant la Sécurisation et JWT]
    // Connexion (/api/players/login)
    @PostMapping("/login")
    public ResponseEntity<PlayerDTO> loginPlayer(@Valid @RequestBody LoginRequest loginRequest) {
        PlayerDTO player = playerService.loginPlayer(loginRequest);
        return ResponseEntity.ok(player);
    }
     */

    // Connexion (/api/players/login)  [(Avec la Sécurisation et JWT)]
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) { // <-- Changé en LoginResponse
        LoginResponse response = playerService.loginPlayer(loginRequest);
        return ResponseEntity.ok(response);
    }

    // Bannir un joueur par son ID
    @PutMapping("/{id}/ban")
    public ResponseEntity<Void> banPlayer(@PathVariable Long id) {
        playerService.banPlayer(id);
        return ResponseEntity.noContent().build();

    }
    // Débannir un joueur par son ID
    @PutMapping("/{id}/deban")
    public ResponseEntity<Void> debanPlayer(@PathVariable Long id) {
      playerService.debanPlayer(id);
      return ResponseEntity.noContent().build();
        }
    }
