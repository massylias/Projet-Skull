package fr.info.orleans.projetinfo.player.config;


import fr.info.orleans.projetinfo.player.model.Player;
import fr.info.orleans.projetinfo.player.repository.PlayerRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final PlayerRepository playerRepository;

    // Injection du repository
    public CustomUserDetailsService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. Chercher le joueur dans la base de données
        Player player = playerRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Joueur non trouvé avec l'email : " + email));

        // 2. Convertir l'entité Player en objet UserDetails
        return User.builder()
                .username(player.getEmail())
                .password(player.getMotDePasse()) // Le mot de passe est déjà haché en BDD
                .roles(player.getRole()) // "USER" ou "ADMIN"
                .disabled(player.getBanni()) // Si banni = true, le compte est désactivé
                .build();
    }



}
