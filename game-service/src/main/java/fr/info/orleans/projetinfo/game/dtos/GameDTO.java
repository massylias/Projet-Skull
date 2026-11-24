package fr.info.orleans.projetinfo.game.dtos;

import fr.info.orleans.projetinfo.game.model.Partie;
import fr.info.orleans.projetinfo.game.model.StatutPartie;

import java.time.LocalDateTime;

public class GameDTO {

    private Long id;
    private StatutPartie statut;
    private int nbJoueurs;
    private int nbIa;
    private Long idCreateur;
    private LocalDateTime dateCreation;
    private int nbParticipants;

    public GameDTO() {
    }

    public GameDTO(Long id, StatutPartie statut, int nbJoueurs, int nbIa,
                   Long idCreateur, LocalDateTime dateCreation, int nbParticipants) {
        this.id = id;
        this.statut = statut;
        this.nbJoueurs = nbJoueurs;
        this.nbIa = nbIa;
        this.idCreateur = idCreateur;
        this.dateCreation = dateCreation;
        this.nbParticipants = nbParticipants;
    }

    public static GameDTO from(Partie partie, int nbParticipants) {
        return new GameDTO(partie.getId(), partie.getStatut(), partie.getNbJoueurs(),
                partie.getNbIa(), partie.getIdCreateur(), partie.getDateCreation(), nbParticipants);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public StatutPartie getStatut() { return statut; }
    public void setStatut(StatutPartie statut) { this.statut = statut; }

    public int getNbJoueurs() { return nbJoueurs; }
    public void setNbJoueurs(int nbJoueurs) { this.nbJoueurs = nbJoueurs; }

    public int getNbIa() { return nbIa; }
    public void setNbIa(int nbIa) { this.nbIa = nbIa; }

    public Long getIdCreateur() { return idCreateur; }
    public void setIdCreateur(Long idCreateur) { this.idCreateur = idCreateur; }

    public LocalDateTime getDateCreation() { return dateCreation; }
    public void setDateCreation(LocalDateTime dateCreation) { this.dateCreation = dateCreation; }

    public int getNbParticipants() { return nbParticipants; }
    public void setNbParticipants(int nbParticipants) { this.nbParticipants = nbParticipants; }
}