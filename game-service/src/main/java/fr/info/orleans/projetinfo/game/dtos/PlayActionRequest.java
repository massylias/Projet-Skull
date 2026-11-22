package fr.info.orleans.projetinfo.game.dtos;

import fr.info.orleans.projetinfo.game.model.TypeTuile;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class PlayActionRequest {
    @NotNull(message = "L'action est obligatoire")
    private TypeAction action;

    private TypeTuile typeTuile;

    @Min(value = 1, message = "L'enchère doit être d'au moins 1")
    private Integer valeur;

    public PlayActionRequest() {
    }

    public PlayActionRequest(TypeAction action, TypeTuile typeTuile, Integer valeur) {
        this.action = action;
        this.typeTuile = typeTuile;
        this.valeur = valeur;
    }

    public TypeAction getAction() {
        return action;
    }

    public void setAction(TypeAction action) {
        this.action = action;
    }

    public TypeTuile getTypeTuile() {
        return typeTuile;
    }

    public void setTypeTuile(TypeTuile typeTuile) {
        this.typeTuile = typeTuile;
    }

    public Integer getValeur() {
        return valeur;
    }

    public void setValeur(Integer valeur) {
        this.valeur = valeur;
    }
}