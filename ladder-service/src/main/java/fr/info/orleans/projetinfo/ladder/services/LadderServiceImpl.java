package fr.info.orleans.projetinfo.ladder.services;

import fr.info.orleans.projetinfo.ladder.dtos.GameHistoryDTO;
import fr.info.orleans.projetinfo.ladder.dtos.GameResultDTO;
import fr.info.orleans.projetinfo.ladder.dtos.PlayerStatsDTO;
import fr.info.orleans.projetinfo.ladder.model.Historique;
import fr.info.orleans.projetinfo.ladder.repository.HistoriqueRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class LadderServiceImpl implements LadderService {

    private final HistoriqueRepository historiqueRepository;



    public LadderServiceImpl(HistoriqueRepository historiqueRepository) {
        this.historiqueRepository = historiqueRepository;
    }

    @Override
    public void recordGameResult(GameResultDTO resultDTO) {
        boolean existeDeja = historiqueRepository.existsByIdPartieAndIdJoueur(
                resultDTO.getIdPartie(),
                resultDTO.getIdJoueur()
        );

        if (existeDeja) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Le résultat pour ce joueur existe déjà pour cette partie.");
        }
        Historique historique = new Historique();
        historique.setScoreFinal(resultDTO.getScoreFinal());
        historique.setTuilesRestantes(resultDTO.getTuilesRestantes());
        historique.setEstVainqueur(resultDTO.isEstVainqueur());
        historique.setDateFin(resultDTO.getDateFin());
        historique.setIdJoueur(resultDTO.getIdJoueur());
        historique.setIdPartie(resultDTO.getIdPartie());

        historiqueRepository.save(historique);
    }

    @Override
    public List<PlayerStatsDTO> getLeaderboard() {
        List<Historique> historiques = historiqueRepository.findAll();

        //  Regroupement par joueur et calcul des statistiques
        return historiques.stream()
                .collect(Collectors.groupingBy(Historique::getIdJoueur))
                .entrySet().stream()
                .map(entry -> {
                    Long playerId = entry.getKey();
                    List<Historique> games = entry.getValue();

                    int totalParties = games.size();
                    int totalVictoires = (int) games.stream().filter(Historique::isEstVainqueur).count();
                    double scoreMoyen = games.stream().mapToInt(Historique::getScoreFinal).average().orElse(0.0);

                    PlayerStatsDTO stats = new PlayerStatsDTO();
                    stats.setIdJoueur(playerId);
                    stats.setTotalParties(totalParties);
                    stats.setTotalVictoires(totalVictoires);
                    stats.setScoreMoyen(scoreMoyen);
                    return stats;
                })
                //  Tri du classement : par victoires décroissantes, puis par score moyen décroissant
                .sorted(Comparator.comparingInt(PlayerStatsDTO::getTotalVictoires).reversed()
                        .thenComparingDouble(PlayerStatsDTO::getScoreMoyen).reversed())
                .collect(Collectors.toList());
    }

    @Override
    public List<GameHistoryDTO> getPlayerHistory(Long playerId) {
        // on recupere l'historique des partie avec le joueur
        List<Historique> playerHistoriques = historiqueRepository.findByIdJoueur(playerId);

        // on extrait la liste des parties du joueur
        List<Long> gameIds = playerHistoriques.stream()
                .map(Historique::getIdPartie)
                .distinct()
                .collect(Collectors.toList());

        // Pour chaque partie, on construie le GameHistoryDTO avec sa liste de résultats
        return gameIds.stream().map(idPartie -> {
            // Récupère tous les résultats de cette partie (tous les participants)
            List<Historique> gameHistoriques = historiqueRepository.findByIdPartie(idPartie);

            // on convertit chaque gameHistorique en GameResultDTO
            List<GameResultDTO> resultDtos = gameHistoriques.stream()
                    .filter(h -> h.getIdJoueur().equals(playerId)) //filtre qui permet de recuperer uniquement les stats du joueurs par partie
                    .map(h -> {
                GameResultDTO rDto = new GameResultDTO();
                rDto.setIdJoueur(h.getIdJoueur());
                rDto.setIdPartie(h.getIdPartie());
                rDto.setScoreFinal(h.getScoreFinal());
                rDto.setTuilesRestantes(h.getTuilesRestantes());
                rDto.setEstVainqueur(h.isEstVainqueur());
                rDto.setDateFin(h.getDateFin());
                return rDto;
            }).collect(Collectors.toList());

            // on rassemble le tout dans le GameHistoryDTO
            GameHistoryDTO historyDto = new GameHistoryDTO();
            historyDto.setIdPartie(idPartie);
            historyDto.setResultats(resultDtos);
            return historyDto;
        }).collect(Collectors.toList());
    }
}