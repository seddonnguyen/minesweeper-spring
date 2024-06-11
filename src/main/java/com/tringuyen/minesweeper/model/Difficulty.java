package com.tringuyen.minesweeper.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Difficulty {
    BEGINNER("Beginner", 8, 8, 10),
    INTERMEDIATE("Intermediate", 16, 16, 40),
    EXPERT("Expert", 30, 16, 99);


    private static final Map<String, Difficulty> BY_NAME = new HashMap<>();

    static {
        for(Difficulty difficulty : values()) {
            BY_NAME.put(difficulty.name.toUpperCase(), difficulty);
        }
    }

    private final String name;
    private final int rows;
    private final int cols;
    private final int mines;

    public static Difficulty valueOfName(String name) {
        return BY_NAME.get(name.toUpperCase());
    }
}