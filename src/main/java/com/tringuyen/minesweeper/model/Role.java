package com.tringuyen.minesweeper.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public enum Role {
    PLAYER("ROLE_PLAYER"),
    ADMIN("ROLE_ADMIN");

    private static final Map<String, Role> BY_NAME = new HashMap<>();

    static {
        for(Role role : values()) {
            BY_NAME.put(role.name, role);
        }
    }

    private final String name;

    public static Role valueOfName(String name) {
        return BY_NAME.get(name.toUpperCase());
    }
}