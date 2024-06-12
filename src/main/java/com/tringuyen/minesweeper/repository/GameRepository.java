package com.tringuyen.minesweeper.repository;

import com.tringuyen.minesweeper.model.Board;
import com.tringuyen.minesweeper.model.Game;
import com.tringuyen.minesweeper.model.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<List<Game>> findByUser(User user);

    @Query("SELECT g.board FROM Game g WHERE g.id = :gameId")
    Optional<Board> findBoardByGameId(@Param("gameId") Long gameId);

    @Transactional
    @Modifying
    @Query("UPDATE Game g SET g.elapsedTimeInSeconds = :seconds WHERE g.id = :gameId")
    void updateElapsedTime(@Param("gameId") Long gameId, @Param("seconds") Long seconds);
}