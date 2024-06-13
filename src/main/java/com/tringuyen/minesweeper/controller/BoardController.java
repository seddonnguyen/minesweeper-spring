package com.tringuyen.minesweeper.controller;

import com.tringuyen.minesweeper.model.Cell;
import com.tringuyen.minesweeper.service.CellService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.logging.Logger;

@RestController
@RequestMapping("/board")
public class BoardController {
    private static final Logger LOGGER = Logger.getLogger(BoardController.class.getName());
    private final CellService cellService;
    private final String username = "admin";

    @Autowired
    public BoardController(CellService cellService) {
        this.cellService = cellService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cell> getCellById(@PathVariable Long id, @RequestParam int row, @RequestParam int col) {
        Optional<Cell> cellOptional = cellService.findByRowAndCol(id, row, col);

        if(cellOptional.isEmpty()) {
            LOGGER.severe("Cell not found with id: " + id);
            return ResponseEntity.notFound()
                                 .build();
        }
        return ResponseEntity.ok(cellOptional.get());

    }
}