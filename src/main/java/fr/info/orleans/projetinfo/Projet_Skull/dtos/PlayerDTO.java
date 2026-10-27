package fr.info.orleans.projetinfo.Projet_Skull.dtos;

public class PlayerDTO {

    private Long id;
    private String email;
    private String role;

    public PlayerDTO(Long id, String email, String role) {
        this.id = id;
        this.email = email;
        this.role = role;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
}
