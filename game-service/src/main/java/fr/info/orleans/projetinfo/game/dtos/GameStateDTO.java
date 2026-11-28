package fr.info.orleans.projetinfo.game.dtos;

import fr.info.orleans.projetinfo.game.model.PhaseDeJeu;
import fr.info.orleans.projetinfo.game.model.StatutPartie;
import fr.info.orleans.projetinfo.game.model.TypeTuile;

import java.util.List;

public class GameStateDTO {

    private Long idPartie;
    private StatutPartie statut;
    private PhaseDeJeu phase;
    private Integer mancheCourante;
    private Integer joueurCourant;
    private Integer enchereMax;
    private List<ParticipantDTO> participants;
    private List<TypeTuile> mesTuilesPosees;

    public GameStateDTO() {
    }

    public GameStateDTO(Long idPartie, StatutPartie statut, PhaseDeJeu phase,
                        Integer mancheCourante, Integer joueurCourant, Integer enchereMax,
                        List<ParticipantDTO> participants, List<TypeTuile> mesTuilesPosees) {
        this.idPartie = idPartie;
        this.statut = statut;
        this.phase = phase;
        this.mancheCourante = mancheCourante;
        this.joueurCourant = joueurCourant;
        this.enchereMax = enchereMax;
        this.participants = participants;
        this.mesTuilesPosees = mesTuilesPosees;
    }

    public Long getIdPartie() {
        return idPartie;
    }

    public void setIdPartie(Long idPartie) {
        this.idPartie = idPartie;
    }

    public StatutPartie getStatut() {
        return statut;
    }

    public void setStatut(StatutPartie statut) {
        this.statut = statut;
    }

    public PhaseDeJeu getPhase() {
        return phase;
    }

    public void setPhase(PhaseDeJeu phase) {
        this.phase = phase;
    }

    public Integer getMancheCourante() {
        return mancheCourante;
    }

    public void setMancheCourante(Integer mancheCourante) {
        this.mancheCourante = mancheCourante;
    }

    public Integer getJoueurCourant() {
        return joueurCourant;
    }

    public void setJoueurCourant(Integer joueurCourant) {
        this.joueurCourant = joueurCourant;
    }

    public Integer getEnchereMax() {
        return enchereMax;
    }

    public void setEnchereMax(Integer enchereMax) {
        this.enchereMax = enchereMax;
    }

    public List<ParticipantDTO> getParticipants() {
        return participants;
    }

    public void setParticipants(List<ParticipantDTO> participants) {
        this.participants = participants;
    }

    public List<TypeTuile> getMesTuilesPosees() {
        return mesTuilesPosees;
    }

    public void setMesTuilesPosees(List<TypeTuile> mesTuilesPosees) {
        this.mesTuilesPosees = mesTuilesPosees;
    }
}