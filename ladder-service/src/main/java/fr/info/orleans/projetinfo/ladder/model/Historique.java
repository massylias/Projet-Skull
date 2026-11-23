package fr.info.orleans.projetinfo.ladder.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

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
    private LocalDateTime dateFin;

    public Historique() {}

    public Historique(int scoreFinal, int tuilesRestantes, boolean estVainqueur, Long idJoueur, Long idPartie, LocalDateTime dateFin) {
        this.scoreFinal = scoreFinal;
        this.tuilesRestantes = tuilesRestantes;
        this.estVainqueur = estVainqueur;
        this.idJoueur = idJoueur;
        this.idPartie = idPartie;
        this.dateFin = dateFin;
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

    public LocalDateTime getDateFin() { return dateFin; }
    public void setDateFin(LocalDateTime dateFin) { this.dateFin = dateFin; }
}