package fr.info.orleans.projetinfo.Projet_Skull.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;

@Entity
@Table(name = "players")
public class Player {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "L'e-mail est obligatoire")
    @Email(message = "Le format de l'e-mail est invalide")
    @Column(nullable = false, unique = true)
    private String email;

    @NotBlank(message = "Le mot de passe est obligatoire")
    @Column(nullable = false)
    private String motDePasse;

    @Column(nullable = false)
    private String role;

    @Column(nullable = false)
    private Boolean banni;

    @Column(nullable = false)
    private LocalDateTime dateInscription;

    // Constructeur vide
    public Player() {}

    // Constructeur complet
    public Player(Long id, String email, String motDePasse, String role, Boolean banni, LocalDateTime dateInscription) {
        this.id = id;
        this.email = email;
        this.motDePasse = motDePasse;
        this.role = role;
        this.banni = banni;
        this.dateInscription = dateInscription;
    }

    @PrePersist
    protected void onCreate() {
        if (this.dateInscription == null) {
            this.dateInscription = LocalDateTime.now();
        }
        if (this.banni == null) {
            this.banni = false;
        }
        if (this.role == null) {
            this.role = "USER";
        }
    }

    // Tout les Getters et Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMotDePasse() { return motDePasse; }
    public void setMotDePasse(String motDePasse) { this.motDePasse = motDePasse; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public Boolean getBanni() { return banni; }
    public void setBanni(Boolean banni) { this.banni = banni; }

    public LocalDateTime getDateInscription() { return dateInscription; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
}