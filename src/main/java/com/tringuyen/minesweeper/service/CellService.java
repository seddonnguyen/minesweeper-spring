package com.tringuyen.minesweeper.service;

import com.tringuyen.minesweeper.model.Cell;
import com.tringuyen.minesweeper.repository.CellRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CellService {
    private final CellRepository cellRepository;

    @Autowired
    public CellService(CellRepository cellRepository) {
        this.cellRepository = cellRepository;
    }

    public void saveAll(List<Cell> cells) {
        cellRepository.saveAll(cells);
    }

    public Optional<Cell> findByRowAndCol(Long boardId, int row, int col) {
        return cellRepository.findByBoardIdAndRowAndCol(boardId, row, col);
    }
}