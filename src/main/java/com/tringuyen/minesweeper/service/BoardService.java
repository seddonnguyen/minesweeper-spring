package com.tringuyen.minesweeper.service;

import com.tringuyen.minesweeper.model.Board;
import com.tringuyen.minesweeper.model.Cell;
import com.tringuyen.minesweeper.repository.BoardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BoardService {
    public static final List<int[]> DIRECTIONS = Arrays.asList(new int[] {-1, -1},
                                                               new int[] {-1, 0},
                                                               new int[] {-1, 1},
                                                               new int[] {0, -1},
                                                               new int[] {0, 1},
                                                               new int[] {1, -1},
                                                               new int[] {1, 0},
                                                               new int[] {1, 1});
    private final BoardRepository boardRepository;
    private final CellService cellService;

    @Autowired
    public BoardService(BoardRepository boardRepository, CellService cellService) {
        this.boardRepository = boardRepository;
        this.cellService = cellService;
    }

    public Board build(int rows, int cols, int mines) {
        if(rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("Board dimensions must be greater than 0");
        }

        if(mines <= 0) {
            throw new IllegalArgumentException("Number of mines must be greater than 0");
        }

        if(mines >= rows * cols) {
            throw new IllegalArgumentException("Number of mines exceeds the board size");
        }

        Board board = new Board();
        board.setRows(rows);
        board.setCols(cols);
        board.setMines(mines);
        board.setFlags(0);
        board.setRevealed(0);
        board.setMineExploded(false);

        List<Cell> cells = board.getCells();
        for(int row = 0; row < rows; row++) {
            for(int col = 0; col < cols; col++) {
                cells.add(new Cell(row, col, board));
            }
        }
        return board;
    }

    public Board initializeMines(Board board, int row, int col) {
        if(!board.isValidPosition(row, col)) {
            throw new IllegalArgumentException("Invalid starting position");
        }

        if(board.getRows() <= 0 || board.getCols() <= 0) {
            throw new IllegalArgumentException("Board dimensions must be greater than 0");
        }

        if(board.getMines() <= 0) {
            throw new IllegalArgumentException("Number of mines must be greater than 0");
        }

        if(board.getMines() >= board.getRows() * board.getCols()) {
            throw new IllegalArgumentException("Number of mines exceeds the board size");
        }

        Map<String, Cell> cellMap = getCellMap(board);

        List<Cell> exclude = getNeighbors(board, row, col);
        exclude.add(cellMap.get(row + "," + col));

        List<Cell> mines = getRandomCells(board.getCells(), exclude, board.getMines());

        for(Cell mine : mines) {
            mine.setIsMine(true);
            for(int[] direction : DIRECTIONS) {
                int newRow = mine.getRow() + direction[0];
                int newCol = mine.getCol() + direction[1];

                if(!board.isValidPosition(newRow, newCol)) { continue; }

                Cell neighbor = cellMap.get(newRow + "," + newCol);
                if(neighbor == null || neighbor.getIsMine()) { continue; }

                neighbor.setAdjacentMine(neighbor.getAdjacentMine() + 1);
            }
        }
        return save(board);
    }

    private Map<String, Cell> getCellMap(Board board) {
        return board.getCells()
                    .stream()
                    .collect(Collectors.toMap(cell -> cell.getRow() + "," + cell.getCol(), cell -> cell));
    }

    public List<Cell> getNeighbors(Board board, int row, int col) {
        Map<String, Cell> cellMap = getCellMap(board);
        return DIRECTIONS.stream()
                         .map(direction -> new int[] {row + direction[0], col + direction[1]})
                         .filter(position -> board.isValidPosition(position[0], position[1]))
                         .map(position -> cellMap.get(position[0] + "," + position[1]))
                         .collect(Collectors.toList());
    }

    private static List<Cell> getRandomCells(List<Cell> cells, List<Cell> exclude, int size) {
        List<Cell> candidates = new ArrayList<>(cells);
        candidates.removeAll(exclude);
        return fisherYatesSampling(candidates, size);
    }

    public Board save(Board board) {
        return boardRepository.save(board);
    }

    private static List<Cell> fisherYatesSampling(List<Cell> cells, int sampleSize) {
        Random random = new Random();
        List<Cell> cellList = new ArrayList<>(cells);
        int currentIndex = cellList.size();

        for(int i = 0; i < sampleSize; i++) {
            int randomIndex = random.nextInt(currentIndex);
            currentIndex--;
            Collections.swap(cellList, currentIndex, randomIndex);
        }
        return cellList.subList(cellList.size() - sampleSize, cellList.size());
    }

    public Board toggleFlag(Board board, int row, int col) {
        if(!board.isValidPosition(row, col)) {
            throw new IllegalArgumentException("Invalid cell position");
        }

        Optional<Cell> cellOptional = cellService.findByRowAndCol(board.getId(), row, col);
        if(cellOptional.isEmpty()) { return board; }

        Cell cell = cellOptional.get();
        if(!cell.isHidden() && !cell.isFlagged()) { return board; }

        cell.toggleFlag();
        board.setFlags(board.getFlags() + (cell.isFlagged() ? 1 : -1));
        return save(board);
    }

    public Board reveal(Board board, int row, int col) {
        Optional<Cell> cellOptional = cellService.findByRowAndCol(board.getId(), row, col);
        if(cellOptional.isEmpty()) { return board; }

        Cell cell = cellOptional.get();
        if(!cell.isHidden()) { return board; }

        if(cell.getIsMine()) {
            cell.reveal();
            board.setMineExploded(true);
            return save(board);
        }

        Map<String, Cell> cellMap = getCellMap(board);
        Queue<Cell> queue = new LinkedList<>();
        queue.add(cell);
        Set<Cell> visited = new HashSet<>();

        while(!queue.isEmpty()) {
            Cell current = queue.poll();

            if(!current.isHidden()) { continue; }
            visited.add(current);

            if(current.getAdjacentMine() == 0) {
                for(int[] direction : DIRECTIONS) {
                    int newRow = current.getRow() + direction[0];
                    int newCol = current.getCol() + direction[1];

                    if(!board.isValidPosition(newRow, newCol)) { continue; }

                    Cell neighbor = cellMap.get(newRow + "," + newCol);
                    if(visited.contains(neighbor) || !neighbor.isHidden()) { continue; }

                    queue.add(neighbor);
                }
            }
        }

        List<Cell> cells = new ArrayList<>(visited);
        board.setRevealed(board.getRevealed() + cells.size());
        cells.forEach(Cell::reveal);
        return save(board);
    }

    public Board revealAllMines(Board board) {
        board.getCells()
             .stream()
             .filter(Cell::getIsMine)
             .forEach(Cell::reveal);
        return save(board);
    }

    public Board revealIncorrectFlags(Board board) {
        board.getCells()
             .stream()
             .filter(cell -> cell.isFlagged() && !cell.getIsMine())
             .forEach(Cell::incorrectFlag);
        return save(board);
    }
}