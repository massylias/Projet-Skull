package fr.info.orleans.projetinfo.player.services.impl;

import fr.info.orleans.projetinfo.player.config.CustomUserDetailsService;
import fr.info.orleans.projetinfo.player.dtos.LoginRequest;
import fr.info.orleans.projetinfo.player.dtos.LoginResponse;
import fr.info.orleans.projetinfo.player.dtos.PlayerDTO;
import fr.info.orleans.projetinfo.player.dtos.RegisterRequest;
import fr.info.orleans.projetinfo.player.jwt.JwtService;
import fr.info.orleans.projetinfo.player.model.Player;
import fr.info.orleans.projetinfo.player.repository.PlayerRepository;
import fr.info.orleans.projetinfo.player.services.PlayerService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PlayerServiceImpl implements PlayerService {

    private final PlayerRepository playerRepository;
    private final PasswordEncoder passwordEncoder;

    private final AuthenticationManager authenticationManager;
    private final JwtService  jwtService;
    private final CustomUserDetailsService customUserDetailsService;


    public PlayerServiceImpl(PlayerRepository playerRepository, PasswordEncoder passwordEncoder, AuthenticationManager authenticationManager, JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.playerRepository = playerRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;

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
        player.setMotDePasse(passwordEncoder.encode(registerRequest.getPassword()));
        player.setRole("USER");
        player.setBanni(false);

        Player savedPlayer = playerRepository.save(player);
        return mapToDTO(savedPlayer);
    }

    /*
     Méthode Avant Sécurisation et JWT
    @Override
    public PlayerDTO loginPlayer(LoginRequest loginRequest) {

        Player player = playerRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(()-> new RuntimeException("Email ou mot de passe incorrect"));

        if (!passwordEncoder.matches(loginRequest.getPassword(), player.getMotDePasse())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }
        if (player.getBanni()) {
            throw new RuntimeException("Le compte de ce  joueur a été banni");
        }
        return mapToDTO(player);

    }

     */

    // [Avec la Sécurisation et JWT]
    @Override
    public LoginResponse loginPlayer(LoginRequest loginRequest) {
        // 1. Authentifier le joueur (vérifie email + mot de passe)
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getEmail(),
                        loginRequest.getPassword()
                )
        );

        // 2. Charger les détails de l'utilisateur
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());

        // 3. Générer le token JWT
        String token = jwtService.generateToken(userDetails);

        // 4. Récupérer les infos du joueur (via ton service existant)
        PlayerDTO player = getPlayerByEmail(loginRequest.getEmail());

        // 5. Retourner la réponse
        return new LoginResponse(token, player);
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