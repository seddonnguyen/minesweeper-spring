package com.tringuyen.minesweeper.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    @NonNull
    private User user;

    @OneToOne(cascade = CascadeType.ALL,
              orphanRemoval = true)
    @JoinColumn(name = "board_id")
    @NonNull
    private Board board;

    @Enumerated(EnumType.STRING)
    @NonNull
    private GameState state;

    @Enumerated(EnumType.STRING)
    @NonNull
    private Difficulty difficulty;

    @NonNull
    private Integer life;

    @NonNull
    private Date startDate;

    @NonNull
    private Date endDate;

    @NonNull
    private Long elapsedTimeInSeconds;

    @PrePersist
    public void prePersist() {
        this.startDate = new Date();
        this.endDate = new Date();
        this.elapsedTimeInSeconds = 0L;
    }

    @PreUpdate
    public void preUpdate() {
        this.endDate = new Date();
    }

    public boolean isGameOver() {
        return state == GameState.WON || state == GameState.LOST;
    }

    public boolean isGameWon() {
        return state == GameState.WON;
    }

    public boolean isGameLost() {
        return state == GameState.LOST;
    }

    public boolean isGameInProgress() {
        return state == GameState.IN_PROGRESS;
    }

    public boolean isGameNew() {
        return state == GameState.NEW;
    }

    public void setGameNew() {
        state = GameState.NEW;
    }

    public void setGameWon() {
        state = GameState.WON;
    }

    public void setGameInProgress() {
        state = GameState.IN_PROGRESS;
    }

    public void decrementLife() {
        life--;
        if(life == 0) { setGameLost(); }
    }

    public void setGameLost() {
        state = GameState.LOST;
    }
}