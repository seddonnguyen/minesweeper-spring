package com.tringuyen.minesweeper.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Cell {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "row_index")
    @NonNull
    @PositiveOrZero
    private Integer row;

    @Column(name = "col_index")
    @NonNull
    @PositiveOrZero
    private Integer col;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "board_id")
    @NonNull
    private Board board;

    @Enumerated(EnumType.STRING)
    @NonNull
    private CellState state;

    @PositiveOrZero
    @NonNull
    private Integer adjacentMine;

    @NonNull
    private Boolean isMine;

    public Cell(@NonNull Integer row, @NonNull Integer col, @NonNull Board board) {
        this.row = row;
        this.col = col;
        this.board = board;
        this.state = CellState.HIDDEN;
        this.adjacentMine = 0;
        this.isMine = false;
    }

    public boolean isRevealed() {
        return state == CellState.REVEALED;
    }

    public boolean isIncorrectlyFlagged() {
        return state == CellState.INCORRECTLY_FLAGGED;
    }

    public void reveal() {
        if(isHidden()) {
            state = isMine ? CellState.EXPLODED : CellState.REVEALED;
        }
    }

    public boolean isHidden() {
        return state == CellState.HIDDEN;
    }

    public void explode() {
        if(isMine && !isExploded()) {
            state = CellState.EXPLODED;
        }
    }

    public boolean isExploded() {
        return state == CellState.EXPLODED;
    }

    public void incorrectFlag() {
        if(!isMine && isFlagged()) {
            state = CellState.INCORRECTLY_FLAGGED;
        }
    }

    public boolean isFlagged() {
        return state == CellState.FLAGGED;
    }

    public void hide() {
        if(!isHidden()) {
            state = CellState.HIDDEN;
        }
    }

    public void toggleFlag() {
        if(!isHidden() && !isFlagged()) { return; }
        state = isFlagged() ? CellState.HIDDEN : CellState.FLAGGED;
    }

    public void setIsMine(Boolean isMine) {
        this.isMine = isMine;
        this.adjacentMine = 0;
    }
}