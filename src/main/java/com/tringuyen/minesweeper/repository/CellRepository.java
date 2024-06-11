package com.tringuyen.minesweeper.repository;

import com.tringuyen.minesweeper.model.Cell;
import com.tringuyen.minesweeper.model.CellState;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CellRepository extends JpaRepository<Cell, Long> {

    @Query("SELECT c FROM Cell c WHERE c.board.id = :boardId AND c.row = :row AND c.col = :col")
    Optional<Cell> findByBoardIdAndRowAndCol(@Param("boardId") Long boardId, @Param("row") int row, @Param("col") int col);

    @Query("SELECT c FROM Cell c WHERE c.board.id = :boardId AND c.state = :state")
    Optional<List<Cell>> findByBoardIdAndState(@Param("boardId") Long boardId, @Param("state") CellState state);

    @Query("SELECT c FROM Cell c WHERE c.board.id = :boardId AND c.isMine = true")
    Optional<List<Cell>> findMineByBoardId(@Param("boardId") Long boardId);
}