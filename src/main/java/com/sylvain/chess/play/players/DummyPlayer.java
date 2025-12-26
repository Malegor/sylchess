package com.sylvain.chess.play.players;

import com.sylvain.chess.Color;
import com.sylvain.chess.moves.Move;

import java.util.List;

/**
 * A player that always plays its first available move.
 */
public class DummyPlayer extends Player {

  public DummyPlayer(Color color) {
    super(color);
  }

  @Override
  protected Move selectMove(List<Move> validMoves) {
    return validMoves.getFirst();
  }
}
