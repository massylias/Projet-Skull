package fr.info.orleans.projetinfo.ladder.dtos;

import java.util.List;

public class GameHistoryDTO {

    private Long idPartie;
    private List<GameResultDTO> resultats;

    public GameHistoryDTO() {}

    public GameHistoryDTO(Long idPartie, List<GameResultDTO> resultats) {
        this.idPartie = idPartie;
        this.resultats = resultats;
    }

    public Long getIdPartie() { return idPartie; }
    public void setIdPartie(Long idPartie) { this.idPartie = idPartie; }

    public List<GameResultDTO> getResultats() { return resultats; }
    public void setResultats(List<GameResultDTO> resultats) { this.resultats = resultats; }
}