package fr.info.orleans.projetinfo.game.dtos;

import jakarta.validation.constraints.Size;

public class JoinGameRequest {
    private Long idPartie;

    @Size(max = 50, message = "Le motif des fleurs ne doit pas dépasser 50 caractères")
    private String motifFleur;

    @Size(max = 50, message = "Le motif du crâne ne doit pas dépasser 50 caractères")
    private String motifCrane;

    public JoinGameRequest() {
    }

    public JoinGameRequest(String motifFleur, String motifCrane) {
        this.motifFleur = motifFleur;
        this.motifCrane = motifCrane;
    }

    public String getMotifFleur() {
        return motifFleur;
    }

    public void setMotifFleur(String motifFleur) {
        this.motifFleur = motifFleur;
    }

    public String getMotifCrane() {
        return motifCrane;
    }

    public void setMotifCrane(String motifCrane) {
        this.motifCrane = motifCrane;
    }
}