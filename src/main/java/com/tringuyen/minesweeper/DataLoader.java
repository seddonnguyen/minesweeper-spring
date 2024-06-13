package com.tringuyen.minesweeper;

import com.tringuyen.minesweeper.model.Role;
import com.tringuyen.minesweeper.model.User;
import com.tringuyen.minesweeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public DataLoader(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        loadAdmin();
        loadPlayer();
    }

    private void loadAdmin() {
        if(userRepository.findByUsername("admin")
                         .isEmpty()) {
            User adminUser = new User();
            adminUser.setUsername("admin");
            adminUser.setPassword(passwordEncoder.encode("password"));
            adminUser.setFirstName("admin");
            adminUser.setLastName("admin");
            adminUser.setEmail("admin@admin.com");
            adminUser.setRole(Role.ADMIN);
            adminUser.setEnabled(true);
            userRepository.save(adminUser);
        }
    }

    private void loadPlayer() {
        if(userRepository.findByUsername("player")
                         .isEmpty()) {
            User player = new User();
            player.setUsername("player");
            player.setPassword(passwordEncoder.encode("password"));
            player.setFirstName("playerFirstName");
            player.setLastName("playerLastName");
            player.setEmail("player@gmail.com");
            player.setRole(Role.PLAYER);
            player.setEnabled(true);
            userRepository.save(player);
        }
    }
}