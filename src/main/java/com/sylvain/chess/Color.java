package com.sylvain.chess;

import lombok.Getter;

import java.util.function.Function;

public enum Color {
    WHITE('w') {
        @Override
        public Function<Character, Character> change() {
            return Character::toUpperCase;
        }
    }, BLACK('b') {
        @Override
        public Function<Character, Character> change() {
            return Character::toLowerCase;
        }
    };

    @Getter
    private final Character fenName;
    Color(final Character fenName) {
        this.fenName = fenName;
    }

    public abstract Function<Character, Character> change();
}
