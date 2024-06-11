package com.tringuyen.minesweeper.controller;

import com.tringuyen.minesweeper.service.GameService;
import com.tringuyen.minesweeper.model.Game;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/game")
public class GameController {
    private final String username = "admin";
    private final GameService gameService;

    public GameController(GameService gameService) { this.gameService = gameService; }

    @PostMapping
    public ResponseEntity<Game> startGame(@RequestParam("difficulty") String difficulty) {
        return ResponseEntity.ok(gameService.create(username, difficulty));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<Game> getGame(@PathVariable("gameId") Long gameId) {
        return ResponseEntity.ok(gameService.getGame(username, gameId));
    }

    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable("gameId") Long gameId) {
        gameService.deleteGame(username, gameId);
        return ResponseEntity.noContent()
                             .build();
    }

    @PutMapping("/{gameId}/reveal")
    public ResponseEntity<Game> reveal(@PathVariable("gameId") Long gameId, @RequestParam int row, @RequestParam int col) {
        return ResponseEntity.ok(gameService.reveal(username, gameId, row, col));
    }

    @PutMapping("/{gameId}/flag")
    public ResponseEntity<Game> flag(@PathVariable("gameId") Long gameId, @RequestParam int row, @RequestParam int col) {
        return ResponseEntity.ok(gameService.flag(username, gameId, row, col));
    }
}