package com.sylvain.chess.play;

public record DrawConditions(int maxNumberOfMovesWithoutCaptureOrPawnMove, int maxNumberOfTimesSamePosition) {
  public boolean tooManyMovesWithoutCaptureOrPawnMove(final GameStateInfo info, final int halfMoveIncrement) {
    return info.getHalfMoveNumber() + halfMoveIncrement - info.getLastHalfMoveWithCaptureOrPawn() > 2 * this.maxNumberOfMovesWithoutCaptureOrPawnMove;
  }
}
