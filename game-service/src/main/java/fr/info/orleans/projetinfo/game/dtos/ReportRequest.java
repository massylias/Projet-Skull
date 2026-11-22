package fr.info.orleans.projetinfo.game.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class ReportRequest {
    @NotNull(message = "L'id du joueur signalé est obligatoire")
    private Long idJoueurSignale;

    @NotBlank(message = "Le motif du signalement est obligatoire")
    @Size(max = 255, message = "Le motif ne doit pas dépasser 255 caractères")
    private String raison;

    public ReportRequest() {
    }

    public ReportRequest(Long idJoueurSignale, String raison) {
        this.idJoueurSignale = idJoueurSignale;
        this.raison = raison;
    }

    public Long getIdJoueurSignale() {
        return idJoueurSignale;
    }

    public void setIdJoueurSignale(Long idJoueurSignale) {
        this.idJoueurSignale = idJoueurSignale;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }
}