package com.sylvain.chess;

import lombok.Getter;

public enum Color {
    WHITE(0), BLACK(1);

    @Getter
    private final int index;
    Color(int index) {
        this.index = index;
    }
}
