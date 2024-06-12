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
import java.util.logging.Logger;

@Service
public class GameService {

    private static final Logger LOGGER = Logger.getLogger(GameService.class.getName());
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
            LOGGER.severe("User not found: " + username);
            throw new ResourceNotFoundException("User", "username", username);
        }

        Difficulty diff = Difficulty.valueOfName(difficulty);

        if(diff == null) {
            LOGGER.severe("Invalid difficulty: " + difficulty);
            throw new IllegalArgumentException("Invalid difficulty: " + difficulty);
        }

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
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if(gameOptional.isEmpty()) {
            LOGGER.severe("Game not found: " + gameId);
            throw new ResourceNotFoundException("Game", "id", gameId);
        }

        Game game = gameOptional.get();
        if(!game.getUser()
                .getUsername()
                .equals(username)) {
            LOGGER.severe("Unauthorized access for User: " + username);
            throw new RuntimeException("Unauthorized access for User: " + username);
        }
        return game;
    }

    public void deleteGame(String username, Long gameId) {
        Game game = getGame(gameId);

        if(!game.getUser()
                .getUsername()
                .equals(username)) {
            LOGGER.severe("Unauthorized access for User: " + username);
            throw new RuntimeException("Unauthorized access for User: " + username);
        }
        gameRepository.delete(game);
    }

    private Game getGame(Long gameId) {
        Optional<Game> gameOptional = gameRepository.findById(gameId);
        if(gameOptional.isEmpty()) {
            LOGGER.severe("Game not found: " + gameId);
            throw new ResourceNotFoundException("Game", "id", gameId);
        }
        return gameOptional.get();
    }

    public Game reveal(String username, Long gameId, int row, int col) {
        Game game = getGame(gameId);

        if(!game.getUser()
                .getUsername()
                .equals(username)) {
            LOGGER.severe("Unauthorized access for User: " + username);
            throw new RuntimeException("Unauthorized access for User: " + username);
        }

        if(game.isGameOver()) { return null; }

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
            LOGGER.severe("Invalid starting position");
            throw new IllegalArgumentException("Invalid starting position");
        }

        game.setGameInProgress();
        boardService.initializeMines(board, row, col);
        return gameRepository.save(game);
    }

    public Game flag(String username, Long gameId, int row, int col) {
        Game game = getGame(gameId);

        if(!game.getUser()
                .getUsername()
                .equals(username)) {
            LOGGER.severe("Unauthorized access for User: " + username);
            throw new RuntimeException("Unauthorized access for User: " + username);
        }

        if(!game.isGameInProgress()) { return game; }

        Board board = boardService.toggleFlag(game.getBoard(), row, col);
        game.setBoard(board);
        return gameRepository.save(game);
    }
}