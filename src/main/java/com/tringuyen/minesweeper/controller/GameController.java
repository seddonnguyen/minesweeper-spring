package com.tringuyen.minesweeper.controller;

import com.tringuyen.minesweeper.model.Game;
import com.tringuyen.minesweeper.payload.request.ElapsedTimeRequest;
import com.tringuyen.minesweeper.service.GameService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.logging.Logger;

@Controller
@RequestMapping("/game")
public class GameController {
    private static final Logger LOGGER = Logger.getLogger(GameController.class.getName());
    private final String username = "admin";
    private final GameService gameService;

    public GameController(GameService gameService) { this.gameService = gameService; }

    @PostMapping
    public ResponseEntity<Game> startGame(@RequestParam("difficulty") String difficulty) {
        return ResponseEntity.ok(gameService.create(username, difficulty));
    }

    @GetMapping
    public String getActiveGame(Model model) {
        model.addAttribute("games", gameService.getActiveGame(username));
        return "games";
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

    @PutMapping("/{gameId}/mark")
    public ResponseEntity<Void> mark(@PathVariable("gameId") Long gameId, @RequestBody ElapsedTimeRequest elapsedTimeRequest) {
        Long elapsedTimeInSeconds = elapsedTimeRequest.getElapsedTimeInSeconds();
        gameService.updateElapsedTime(username, gameId, elapsedTimeInSeconds);
        return ResponseEntity.noContent()
                             .build();
    }

    @PutMapping("/{gameId}/flag")
    public ResponseEntity<Game> flag(@PathVariable("gameId") Long gameId, @RequestParam int row, @RequestParam int col) {
        Game game = gameService.flag(username, gameId, row, col);
        if(!game.isGameInProgress()) {
            return ResponseEntity.noContent()
                                 .build();
        }
        return ResponseEntity.ok(game);
    }
}