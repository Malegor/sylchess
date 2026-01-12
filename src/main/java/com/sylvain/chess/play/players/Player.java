package com.sylvain.chess.play.players;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public abstract class Player {
  @Getter
  private final Color color;
  @Getter
  private final String name;
  private final ChessBoard board;

  public Move move() {
    final List<Move> validMoves = this.board.findAllValidMoves(this.color);
    if (validMoves.isEmpty())
      return null;
    final Move selectedMove = this.selectMove(validMoves);
    if (!validMoves.contains(selectedMove))
      throw new IllegalArgumentException("Invalid move " + selectedMove);
    return selectedMove;
  }

  protected abstract Move selectMove(final List<Move> validMoves);
}
