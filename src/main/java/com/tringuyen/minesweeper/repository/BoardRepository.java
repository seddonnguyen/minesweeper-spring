package com.tringuyen.minesweeper.repository;

import com.tringuyen.minesweeper.model.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> { }