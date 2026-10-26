package fr.info.orleans.projetinfo.Projet_Skull.controller;
import fr.info.orleans.projetinfo.Projet_Skull.model.Player;
import fr.info.orleans.projetinfo.Projet_Skull.services.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/players")
public class PlayerController {

    private final PlayerService playerService;

    @Autowired
    public PlayerController(PlayerService playerService) {
        this.playerService = playerService;
    }

    // Récupérer la liste de tous les joueurs
    @GetMapping
    public List<Player> getAllPlayers() {
        return playerService.getAllPlayers();
    }

    // Récupérer un joueur par son ID
    @GetMapping("/{id}")
    public ResponseEntity<Player> getPlayerById(@PathVariable Long id) {
        return playerService.getPlayerById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }


    // Inscription d'un joueur (/api/players/register)
    @PostMapping("/register")
    public Player registerPlayer(@RequestBody Player player) {
        // Les valeurs par défaut (rôle USER, banni = false, dateInscription)
        // sont gérées automatiquement par l'entité grâce à @PrePersist
        return playerService.savePlayer(player);
    }

    // Connexion (/api/players/login)
    @PostMapping("/login")
    public ResponseEntity<String> loginPlayer(@RequestBody Player loginRequest) {
        return playerService.getAllPlayers().stream()
                .filter(p -> p.getEmail().equals(loginRequest.getEmail()) &&
                        p.getMotDePasse().equals(loginRequest.getMotDePasse()))
                .findFirst()
                .map(p -> ResponseEntity.ok("Connexion réussie pour : " + p.getEmail()))
                .orElse(ResponseEntity.status(401).body("Identifiants invalides"));
    }

    // Bannir un joueur par son ID
    @PutMapping("/{id}/ban")
    public ResponseEntity<Player> banPlayer(@PathVariable Long id) {
        try {
            Player playerBanni = playerService.banPlayer(id);
            return ResponseEntity.ok(playerBanni);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PutMapping("/{id}/deban")
    public ResponseEntity<Player> banPlayer(@PathVariable Long id, @RequestBody Player banRequest) {
        try {
            Player playerBanni = playerService.debanPlayer(id);
            return ResponseEntity.ok(playerBanni);
        }catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }


}