package fr.info.orleans.projetinfo.game.controller;

import fr.info.orleans.projetinfo.game.model.Signalement;
import fr.info.orleans.projetinfo.game.repository.SignalementRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/signalements")
public class SignalementController {

    private final SignalementRepository signalementRepository;

    public SignalementController(SignalementRepository signalementRepository) {
        this.signalementRepository = signalementRepository;
    }
    // Liste des signalements non traités (pour l'admin)
    @GetMapping
    public ResponseEntity<List<Signalement>> getSignalementsNonTraites() {
        return ResponseEntity.ok(signalementRepository.findByTraiteOrderByDateSignalementDesc(false));
    }

    // (Optionnel) Marquer un signalement comme traité
    @PutMapping("/{id}/traiter")
    public ResponseEntity<Void> traiterSignalement(@PathVariable Long id) {
        Signalement signalement = signalementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Signalement introuvable"));
        signalement.setTraite(true);
        signalementRepository.save(signalement);
        return ResponseEntity.ok().build();
    }



}
