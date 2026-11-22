package fr.info.orleans.projetinfo.game.model;

import jakarta.persistence.*;

@Entity
@Table(name = "participation")
public class Participation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idPartie;

    private Long idJoueur;

    private boolean estIa = false;

    private boolean aQuitte = false;

    private int score = 0;

    private int tuilesRestantes = 4;

    private int position;


    public Participation() {
    }


    public Long getIdPartie() {
        return idPartie;
    }

    public void setIdPartie(Long idPartie) {
        this.idPartie = idPartie;
    }

    public Long getIdJoueur() {
        return idJoueur;
    }

    public void setIdJoueur(Long idJoueur) {
        this.idJoueur = idJoueur;
    }

    public boolean isEstIa() {
        return estIa;
    }

    public void setEstIa(boolean estIa) {
        this.estIa = estIa;
    }

    public boolean isaQuitte() {
        return aQuitte;
    }

    public void setaQuitte(boolean aQuitte) {
        this.aQuitte = aQuitte;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTuilesResistantes() {
        return tuilesRestantes;
    }

    public void setTuilesResistantes(int tuilesResistantes) {
        this.tuilesRestantes = tuilesResistantes;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public Long getId() {
        return id;
    }


}
