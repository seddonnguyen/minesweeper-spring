package com.tringuyen.minesweeper.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@AllArgsConstructor
public class Board {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "num_rows")
    @NonNull
    @Positive
    private Integer rows;

    @Column(name = "num_cols")
    @NonNull
    @Positive
    private Integer cols;

    @NonNull
    @Positive
    private Integer mines;

    @NonNull
    @PositiveOrZero
    private Integer revealed;

    @NonNull
    @PositiveOrZero
    private Integer flags;

    @NonNull
    private Boolean mineExploded;

    @OneToMany(mappedBy = "board",
               cascade = CascadeType.ALL,
               orphanRemoval = true)
    @JsonManagedReference
    @NonNull
    private List<Cell> cells;

    public Board() {
        this.rows = 0;
        this.cols = 0;
        this.mines = 0;
        this.cells = new ArrayList<>();
        this.revealed = 0;
        this.flags = 0;
        this.mineExploded = false;
    }

    public Integer getRemainingMines() {
        return this.mines - this.flags;
    }

    public boolean isValidPosition(int row, int col) {
        return row >= 0 && row < this.rows && col >= 0 && col < this.cols;
    }

    public boolean hasMoves() {
        int totalMoves = this.rows * this.cols - this.mines;
        int availableMoves = totalMoves - this.revealed;
        return availableMoves > 0;
    }
}