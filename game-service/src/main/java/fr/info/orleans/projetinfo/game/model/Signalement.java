package fr.info.orleans.projetinfo.game.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Table(name = "signalement")
@Entity
public class Signalement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idPartie;

    @Column(nullable = false)
    private Long idJoueurSignale;

    @Column(nullable = false)
    private Long idJoueurSignaleur;

    @Column(nullable = false, length = 255)
    private String raison;

    @Column(nullable = false)
    private LocalDateTime dateSignalement = LocalDateTime.now();

    @Column(nullable = false)
    private boolean traite = false;

    public Signalement() {
    }

    public Long getIdPartie() {
        return idPartie;
    }

    public void setIdPartie(Long idPartie) {
        idPartie = idPartie;
    }

    public Long getIdJoueurSignale() {
        return idJoueurSignale;
    }

    public void setIdJoueurSignale(Long idJoueurSignale) {
        this.idJoueurSignale = idJoueurSignale;
    }

    public Long getIdJoueurSignaleur() {
        return idJoueurSignaleur;
    }

    public void setIdJoueurSignaleur(Long idJoueurSignaleur) {
        this.idJoueurSignaleur = idJoueurSignaleur;
    }

    public String getRaison() {
        return raison;
    }

    public void setRaison(String raison) {
        this.raison = raison;
    }

    public LocalDateTime getDateSignalement() {
        return dateSignalement;
    }

    public void setDateSignalement(LocalDateTime dateSignalement) {
        this.dateSignalement = dateSignalement;
    }

    public boolean isTraite() {
        return traite;
    }

    public void setTraite(boolean traite) {
        this.traite = traite;
    }

    public Long getId() {
        return id;
    }

}
