package fr.info.orleans.projetinfo.Projet_Skull.dtos;

import java.time.LocalDateTime;

public class PlayerDTO {

    private Long id;
    private String email;
    private String role;
    private Boolean banni;
    private LocalDateTime dateInscription;

    public PlayerDTO() {
    }

    public PlayerDTO(Long id, String email, String role, Boolean banni, LocalDateTime dateInscription) {
        this.id = id;
        this.email = email;
        this.role = role;
        this.banni = banni;
        this.dateInscription = dateInscription;
    }

    // Getters
    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public Boolean getBanni() { return banni; }
    public LocalDateTime getDateInscription() { return dateInscription; }

    // Setters
    public void setId(Long id) { this.id = id; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setBanni(Boolean banni) { this.banni = banni; }
    public void setDateInscription(LocalDateTime dateInscription) { this.dateInscription = dateInscription; }
}
