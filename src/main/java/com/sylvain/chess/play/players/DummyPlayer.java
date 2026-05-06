package com.sylvain.chess.play.players;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;

import java.util.List;

/**
 * A player that always plays its first available move.
 */
public class DummyPlayer extends Player {

  public DummyPlayer(final PlayerColor color, final ChessBoard board) {
    super(color, DummyPlayer.class.getSimpleName(), board);
  }

  @Override
  protected Move selectMove(final List<Move> validMoves) {
    return validMoves.getFirst();
  }
}
