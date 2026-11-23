package fr.info.orleans.projetinfo.ladder.model;

import jakarta.persistence.*;

@Entity
@Table(name = "historique")
public class Historique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int scoreFinal;
    private int tuilesRestantes;
    private boolean estVainqueur;
    private Long idJoueur;
    private Long idPartie;

    public Historique() {}

    public Historique(int scoreFinal, int tuilesRestantes, boolean estVainqueur, Long idJoueur, Long idPartie) {
        this.scoreFinal = scoreFinal;
        this.tuilesRestantes = tuilesRestantes;
        this.estVainqueur = estVainqueur;
        this.idJoueur = idJoueur;
        this.idPartie = idPartie;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public int getScoreFinal() { return scoreFinal; }
    public void setScoreFinal(int scoreFinal) { this.scoreFinal = scoreFinal; }

    public int getTuilesRestantes() { return tuilesRestantes; }
    public void setTuilesRestantes(int tuilesRestantes) { this.tuilesRestantes = tuilesRestantes; }

    public boolean isEstVainqueur() { return estVainqueur; }
    public void setEstVainqueur(boolean estVainqueur) { this.estVainqueur = estVainqueur; }

    public Long getIdJoueur() { return idJoueur; }
    public void setIdJoueur(Long idJoueur) { this.idJoueur = idJoueur; }

    public Long getIdPartie() { return idPartie; }
    public void setIdPartie(Long idPartie) { this.idPartie = idPartie; }
}
