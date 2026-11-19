package fr.info.orleans.projetinfo.player.dtos;

public class LoginResponse {

    private String token;
    private PlayerDTO player;

    public LoginResponse() {
    }

    public LoginResponse(String token, PlayerDTO player) {
        this.token = token;
        this.player = player;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public PlayerDTO getPlayer() {
        return player;
    }

    public void setPlayer(PlayerDTO player) {
        this.player = player;
    }
}
