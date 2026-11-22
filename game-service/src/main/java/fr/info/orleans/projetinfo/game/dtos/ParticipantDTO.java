package fr.info.orleans.projetinfo.game.dtos;

public class ParticipantDTO {

    private int positionTable;
    private Long idJoueur;
    private boolean estIa;
    private boolean aQuitte;
    private int score;
    private int tuilesRestantes;
    private int nbTuilesPosees;

    public ParticipantDTO() {
    }

    public ParticipantDTO(int positionTable, Long idJoueur, boolean estIa, boolean aQuitte,
                          int score, int tuilesRestantes, int nbTuilesPosees) {
        this.positionTable = positionTable;
        this.idJoueur = idJoueur;
        this.estIa = estIa;
        this.aQuitte = aQuitte;
        this.score = score;
        this.tuilesRestantes = tuilesRestantes;
        this.nbTuilesPosees = nbTuilesPosees;
    }

    public int getPositionTable() {
        return positionTable;
    }

    public void setPositionTable(int positionTable) {
        this.positionTable = positionTable;
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

    public boolean isAQuitte() {
        return aQuitte;
    }

    public void setAQuitte(boolean aQuitte) {
        this.aQuitte = aQuitte;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getTuilesRestantes() {
        return tuilesRestantes;
    }

    public void setTuilesRestantes(int tuilesRestantes) {
        this.tuilesRestantes = tuilesRestantes;
    }

    public int getNbTuilesPosees() {
        return nbTuilesPosees;
    }

    public void setNbTuilesPosees(int nbTuilesPosees) {
        this.nbTuilesPosees = nbTuilesPosees;
    }
}