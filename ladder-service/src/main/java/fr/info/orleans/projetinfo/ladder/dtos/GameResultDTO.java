package fr.info.orleans.projetinfo.ladder.dtos;

public class GameResultDTO {

    private Long idPartie;
    private Long idJoueur;
    private int scoreFinal;
    private int tuilesRestantes;
    private boolean estVainqueur;

    public GameResultDTO() {}

    public GameResultDTO(Long idPartie, Long idJoueur, int scoreFinal, int tuilesRestantes, boolean estVainqueur) {
        this.idPartie = idPartie;
        this.idJoueur = idJoueur;
        this.scoreFinal = scoreFinal;
        this.tuilesRestantes = tuilesRestantes;
        this.estVainqueur = estVainqueur;
    }

    public Long getIdPartie() { return idPartie; }
    public void setIdPartie(Long idPartie) { this.idPartie = idPartie; }

    public Long getIdJoueur() { return idJoueur; }
    public void setIdJoueur(Long idJoueur) { this.idJoueur = idJoueur; }

    public int getScoreFinal() { return scoreFinal; }
    public void setScoreFinal(int scoreFinal) { this.scoreFinal = scoreFinal; }

    public int getTuilesRestantes() { return tuilesRestantes; }
    public void setTuilesRestantes(int tuilesRestantes) { this.tuilesRestantes = tuilesRestantes; }

    public boolean isEstVainqueur() { return estVainqueur; }
    public void setEstVainqueur(boolean estVainqueur) { this.estVainqueur = estVainqueur; }
}
