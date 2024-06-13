package com.tringuyen.minesweeper.repository;

import com.tringuyen.minesweeper.model.Role;
import com.tringuyen.minesweeper.model.User;
import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Boolean existsByUsername(String username);

    Boolean existsByEmail(String email);

    Optional<User> findByRole(@NonNull Role role);
}