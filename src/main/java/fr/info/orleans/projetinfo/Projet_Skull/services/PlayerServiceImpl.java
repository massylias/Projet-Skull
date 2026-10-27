package fr.info.orleans.projetinfo.Projet_Skull.services;

import fr.info.orleans.projetinfo.Projet_Skull.dtos.LoginRequest;
import fr.info.orleans.projetinfo.Projet_Skull.dtos.PlayerDTO;
import fr.info.orleans.projetinfo.Projet_Skull.dtos.RegisterRequest;
import fr.info.orleans.projetinfo.Projet_Skull.model.Player;
import fr.info.orleans.projetinfo.Projet_Skull.repository.PlayerRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;


    public PlayerServiceImpl(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public List<PlayerDTO> getAllPlayers() {
        return playerRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public PlayerDTO getPlayerById(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Joueur introuvable"));
        return mapToDTO(player);
    }

    @Override
    public PlayerDTO getPlayerByEmail(String email) {
        Player player = playerRepository.findByEmail(email)
                .orElseThrow(()-> new RuntimeException("Joueur introuvable"));
        return mapToDTO(player);
    }

    @Override
    public PlayerDTO savePlayer(RegisterRequest registerRequest) {
        if(playerRepository.existsByEmail(registerRequest.getEmail())) {
            throw new RuntimeException("Un compte existe déjà avec cet émail ");
        }
        Player player = new Player();
        player.setEmail(registerRequest.getEmail());
        player.setMotDePasse((registerRequest.getPassword()));
        player.setRole("USER");
        player.setBanni(false);

        Player savedPlayer = playerRepository.save(player);
        return mapToDTO(savedPlayer);
    }

    @Override
    public PlayerDTO loginPlayer(LoginRequest loginRequest) {

        Player player = playerRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new RuntimeException("Email ou mot de passe incorrect"));

        if (!loginRequest.getPassword().equals(player.getMotDePasse())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }
        if (player.getBanni()) {
            throw new RuntimeException("Le compte de ce  joueur est banni");
        }
        return mapToDTO(player);

    }


    @Override
    public void banPlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Joueur non trouvé avec l'id : " + id));

        player.setBanni(true);
        playerRepository.save(player);
    }

    @Override
    public void debanPlayer(Long id) {
        Player player = playerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Joueur non trouva avec l'id : " + id));

        player.setBanni(false);
         playerRepository.save(player);
    }
    // Méthode pour convertir les Entités en DTO
    private  PlayerDTO mapToDTO(Player player) {
        return new PlayerDTO(
                player.getId(),
                player.getEmail(),
                player.getRole(),
                player.getBanni(),
                player.getDateInscription()
        );
    }


}