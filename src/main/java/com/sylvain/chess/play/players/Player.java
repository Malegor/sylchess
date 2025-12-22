package com.sylvain.chess.play.players;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Set;

@AllArgsConstructor
@Getter
public abstract class Player {
  private final Color color;

  public Move move(final ChessBoard board) {
    final Set<Move> validMoves = board.getAllValidMoves(this.color);
    return validMoves.isEmpty() ? null : this.selectMove(validMoves);
  }

  protected abstract Move selectMove(Set<Move> validMoves);
}
