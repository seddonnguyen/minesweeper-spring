package com.tringuyen.minesweeper.controller;

import com.tringuyen.minesweeper.model.Cell;
import com.tringuyen.minesweeper.service.CellService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/board")
public class BoardController {
    private final CellService cellService;
    private final String username = "admin";

    public BoardController(CellService cellService) {
        this.cellService = cellService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cell> getCellById(@PathVariable Long id, @RequestParam int row, @RequestParam int col) {
        Optional<Cell> cell = cellService.findByRowAndCol(id, row, col);
        return cell.map(ResponseEntity::ok)
                   .orElseGet(() -> ResponseEntity.notFound()
                                                  .build());
    }
}