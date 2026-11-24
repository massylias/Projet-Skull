package fr.info.orleans.projetinfo.game.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enchere")
public class Enchere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idPartie;

    @Column(nullable = false)
    private Long idParticipation;

    @Column(nullable = false)
    private int valeur;

    @Column(nullable = false)
    private int manche;

    @Column(nullable = false)
    private boolean retire = false;

    @Column(nullable = false)
    private LocalDateTime dateEnchere = LocalDateTime.now();

    public Enchere() {
    }

    public Long getIdPartie() {
        return idPartie;
    }

    public void setIdPartie(Long idpartie) {
        this.idPartie = idpartie;
    }

    public Long getIdParticipation() {
        return idParticipation;
    }

    public void setIdParticipation(Long idParticipation) {
        this.idParticipation = idParticipation;
    }

    public int getValeur() {
        return valeur;
    }

    public void setValeur(int valeur) {
        this.valeur = valeur;
    }

    public int getManche() {
        return manche;
    }

    public void setManche(int manche) {
        this.manche = manche;
    }

    public boolean isRetire() {
        return retire;
    }

    public void setRetire(boolean retire) {
        this.retire = retire;
    }

    public LocalDateTime getDateEnchere() {
        return dateEnchere;
    }

    public void setDateEnchere(LocalDateTime dateEnchere) {
        this.dateEnchere = dateEnchere;
    }

    public Long getId() {
        return id;
    }
}
