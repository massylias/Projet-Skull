package fr.info.orleans.projetinfo.ladder.dtos;

public class PlayerStatsDTO {

    private Long idJoueur;
    private int totalParties;
    private int totalVictoires;
    private double scoreMoyen;

    public PlayerStatsDTO() {}

    public PlayerStatsDTO(Long idJoueur, int totalParties, int totalVictoires, double scoreMoyen) {
        this.idJoueur = idJoueur;
        this.totalParties = totalParties;
        this.totalVictoires = totalVictoires;
        this.scoreMoyen = scoreMoyen;
    }

    public Long getIdJoueur() { return idJoueur; }
    public void setIdJoueur(Long idJoueur) { this.idJoueur = idJoueur; }

    public int getTotalParties() { return totalParties; }
    public void setTotalParties(int totalParties) { this.totalParties = totalParties; }

    public int getTotalVictoires() { return totalVictoires; }
    public void setTotalVictoires(int totalVictoires) { this.totalVictoires = totalVictoires; }

    public double getScoreMoyen() { return scoreMoyen; }
    public void setScoreMoyen(double scoreMoyen) { this.scoreMoyen = scoreMoyen; }
}