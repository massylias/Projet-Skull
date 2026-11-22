package fr.info.orleans.projetinfo.game.dtos;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreateGameRequest {

    @NotNull(message = "Le nombre de joueurs est obligatoire")
    @Min(value = 3, message = "Il faut au moins 3 joueurs")
    @Max(value = 6, message = "Il ne peut pas y avoir plus de 6 joueurs")
    private Integer nbJoueurs;

    @NotNull(message = "Le nombre d'IA est obligatoire")
    @Min(value = 0, message = "Le nombre d'IA ne peut pas être négatif")
    @Max(value = 5, message = "Il ne peut pas y avoir plus de 5 IA")
    private Integer nbIa;

    public CreateGameRequest() {
    }

    public CreateGameRequest(Integer nbJoueurs, Integer nbIa) {
        this.nbJoueurs = nbJoueurs;
        this.nbIa = nbIa;
    }

    public Integer getNbJoueurs() {
        return nbJoueurs;
    }

    public void setNbJoueurs(Integer nbJoueurs) {
        this.nbJoueurs = nbJoueurs;
    }

    public Integer getNbIa() {
        return nbIa;
    }

    public void setNbIa(Integer nbIa) {
        this.nbIa = nbIa;
    }
}
