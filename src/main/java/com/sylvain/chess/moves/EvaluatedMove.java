package com.sylvain.chess.moves;

public record EvaluatedMove(Move move, double evaluation) {
  @Override
  public String toString() {
    return this.move + ", evaluation=" + this.evaluation;
  }
}
