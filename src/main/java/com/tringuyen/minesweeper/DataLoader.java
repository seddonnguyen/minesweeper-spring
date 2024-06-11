package com.tringuyen.minesweeper;

import com.tringuyen.minesweeper.model.Role;
import com.tringuyen.minesweeper.model.User;
import com.tringuyen.minesweeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final UserRepository userRepository;

    @Autowired
    public DataLoader(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        loadAdmin();
        loadPlayer();
    }

    private void loadAdmin() {
        if(userRepository.findByUsername("admin")
                         .isEmpty()) {
            User adminUser = new User("admin", "password", "admin", "admin", "admin@minesweeper.com", Role.ADMIN);
            userRepository.save(adminUser);
        }
    }

    private void loadPlayer() {
        if(userRepository.findByUsername("player")
                         .isEmpty()) {
            User user = new User("player", "password", "player", "player", "user@minesweeper.com", Role.PLAYER);
            userRepository.save(user);
        }
    }
}