package com.sylvain.chess.play;

import lombok.Getter;

public enum EndGame {
  WHITE_WINS("1-0"),
  BLACK_WINS("0-1"),
  DRAW("1/2-1/2"),
  STILL_PLAYING(""),
  ERROR("*");

  @Getter
  private final String pgn;
  EndGame(final String pgn) {
    this.pgn = pgn;
  }
}
