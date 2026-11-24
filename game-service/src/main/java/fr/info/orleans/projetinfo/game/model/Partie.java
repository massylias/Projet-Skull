package fr.info.orleans.projetinfo.game.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "partie")
public class Partie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutPartie statut = StatutPartie.EN_ATTENTE;

    @Column(nullable = false)
    private  int nbJoueurs;

    @Column(nullable = false)
    private int nbIa;

    @Column(nullable = false)
    private Long idCreateur;

    @Column(nullable = false)
    private LocalDateTime dateCreation = LocalDateTime.now();
    private LocalDateTime dateFin;

    @Enumerated(EnumType.STRING)
    private PhaseDeJeu phase;

    private Integer mancheCourante;

    private Integer joueurCourant;

    private Integer enchereMax;

    public Partie() {
    }

    public StatutPartie getStatut() {
        return statut;
    }

    public void setStatut(StatutPartie statut) {
        this.statut = statut;
    }


    public int getNbIa() {
        return nbIa;
    }

    public void setNbIa(int nbIa) {
        this.nbIa = nbIa;
    }

    public Long getIdCreateur() {
        return idCreateur;
    }

    public void setIdCreateur(Long idCreateur) {
        this.idCreateur = idCreateur;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDateFin() {
        return dateFin;
    }

    public Integer getEnchereMax() {
        return enchereMax;
    }

    public void setEnchereMax(Integer enchereMax) {
        this.enchereMax = enchereMax;
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

    public PhaseDeJeu getPhase() {
        return phase;
    }

    public void setPhase(PhaseDeJeu phase) {
        this.phase = phase;
    }

    public int getNbJoueurs() {
        return nbJoueurs;
    }

    public void setNbJoueurs(int nbJoueurs) {
        this.nbJoueurs = nbJoueurs;
    }

    public void setDateFin(LocalDateTime dateFin) {
        this.dateFin = dateFin;
    }

    public Long getId() {
        return id;
    }

}
