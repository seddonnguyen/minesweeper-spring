package com.tringuyen.minesweeper.repository;

import com.tringuyen.minesweeper.model.Game;
import com.tringuyen.minesweeper.model.GameState;
import com.tringuyen.minesweeper.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<List<Game>> findByUser(User user);

    @Query("SELECT g FROM Game g WHERE g.user = :user ORDER BY g.endDate DESC")
    List<Game> findByUserOrderByEndDateDesc(@Param("user") User user);

    @Transactional
    @Modifying
    @Query("UPDATE Game g SET g.elapsedTimeInSeconds = :seconds WHERE g.id = :gameId")
    void updateElapsedTime(@Param("gameId") Long gameId, @Param("seconds") Long seconds);
}