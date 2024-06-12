package com.tringuyen.minesweeper.payload.request;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class ElapsedTimeRequest {
    @NonNull
    @PositiveOrZero
    private Long elapsedTimeInSeconds;
}