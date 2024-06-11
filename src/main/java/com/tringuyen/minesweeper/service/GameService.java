package com.tringuyen.minesweeper.service;

import com.tringuyen.minesweeper.model.Board;
import com.tringuyen.minesweeper.model.Difficulty;
import com.tringuyen.minesweeper.model.Game;
import com.tringuyen.minesweeper.model.User;
import com.tringuyen.minesweeper.exception.ResourceNotFoundException;
import com.tringuyen.minesweeper.repository.GameRepository;
import com.tringuyen.minesweeper.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final BoardService boardService;

    @Autowired
    public GameService(GameRepository gameRepository, UserRepository userRepository, BoardService boardService) {
        this.gameRepository = gameRepository;
        this.userRepository = userRepository;
        this.boardService = boardService;
    }

    public Game create(String username, String difficulty) {
        Optional<User> user = userRepository.findByUsername(username);
        if(user.isEmpty()) {
            throw new ResourceNotFoundException("User", "username", username);
        }

        Difficulty diff = Difficulty.valueOfName(difficulty);
        Board board = boardService.build(diff.getRows(), diff.getCols(), diff.getMines());
        Game game = new Game();
        game.setGameNew();
        game.setUser(user.get());
        game.setDifficulty(diff);
        game.setBoard(board);
        game.setLife(3);
        gameRepository.save(game);
        return game;
    }

    public Game getGame(String username, Long gameId) {
        Game game = gameRepository.findById(gameId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));
        if(!game.getUser()
                .getUsername()
                .equals(username)) {
            throw new RuntimeException("Unauthorized access for User: " + username);
        }
        return game;
    }

    public void deleteGame(String username, Long gameId) {
        Game game = gameRepository.findById(gameId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));
        if(!game.getUser()
                .getUsername()
                .equals(username)) {
            throw new RuntimeException("Unauthorized access for User: " + username);
        }
        gameRepository.delete(game);
    }

    public Game reveal(String username, Long gameId, int row, int col) {
        Game game = gameRepository.findById(gameId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));
        if(!game.getUser()
                .getUsername()
                .equals(username)) {
            throw new RuntimeException("Unauthorized access for User: " + username);
        }

        if(game.isGameNew()) {
            game = populateMines(game, row, col);
        }

        Board board = boardService.reveal(game.getBoard(), row, col);
        if(board.getMineExploded()) {
            game.setLife(game.getLife() - 1);
            if(game.getLife() == 0) {
                game.setGameLost();
                boardService.revealAllMines(board);
                boardService.revealIncorrectFlags(board);
            } else {
                board.setMineExploded(false);
            }
        }
        game.setBoard(board);

        if(!board.hasMoves()) {
            game.setGameWon();
        }
        return gameRepository.save(game);
    }

    private Game populateMines(Game game, int row, int col) {
        if(!game.isGameNew()) { return game; }
        Board board = game.getBoard();

        if(!board.isValidPosition(row, col)) {
            throw new IllegalArgumentException("Invalid starting position");
        }

        game.setGameInProgress();
        boardService.initializeMines(board, row, col);
        return gameRepository.save(game);
    }

    public Game flag(String username, Long gameId, int row, int col) {
        Game game = gameRepository.findById(gameId)
                                  .orElseThrow(() -> new ResourceNotFoundException("Game", "id", gameId));
        if(!game.getUser()
                .getUsername()
                .equals(username)) {
            throw new RuntimeException("Unauthorized access for User: " + username);
        }

        if(game.isGameNew()) { return game; }

        Board board = boardService.toggleFlag(game.getBoard(), row, col);
        game.setBoard(board);
        return gameRepository.save(game);
    }
}