package com.sylvain.chess.board;

import lombok.Getter;

public enum GameVariant {
  UNKNOWN("Unknown"), CLASSICAL("Classical"), CHESS960("Chess960");
  @Getter
  private final String value;

  GameVariant(final String value) {
    this.value = value;
  }
}
