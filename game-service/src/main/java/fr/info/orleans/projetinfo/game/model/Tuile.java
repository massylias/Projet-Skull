package fr.info.orleans.projetinfo.game.model;

import jakarta.persistence.*;

    @Entity
    @Table(name = "tuile")
    public class Tuile {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        @Column(nullable = false)
        private Long idPartie;

        @Column(nullable = false)
        private Long idJoueur;

        @Enumerated(EnumType.STRING)
        @Column(nullable = false)
        private TypeTuile type;

        @Column(nullable = false)
        private int position; // Ordre dans la pile (0 = bas, 3 = haut)

        @Column(nullable = false)
        private boolean faceCachee = true; // true = dans la main, false = posée sur la table

        public Tuile() {
        }

        public Tuile(Long idPartie, Long idJoueur, TypeTuile type, int position) {
            this.idPartie = idPartie;
            this.idJoueur = idJoueur;
            this.type = type;
            this.position = position;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
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

        public TypeTuile getType() {
            return type;
        }

        public void setType(TypeTuile type) {
            this.type = type;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public boolean isFaceCachee() {
            return faceCachee;
        }

        public void setFaceCachee(boolean faceCachee) {
            this.faceCachee = faceCachee;
        }
    }
