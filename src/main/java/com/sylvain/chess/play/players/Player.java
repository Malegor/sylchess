package com.sylvain.chess.play.players;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public abstract class Player {
  @Getter
  protected final PlayerColor color;
  @Getter
  protected final String name;
  protected final ChessBoard board;

  public Move getSelectedMove() {
    final List<Move> validMoves = this.board.findAllValidMoves(this.color);
    if (validMoves.isEmpty())
      return null;
    final Move selectedMove = this.selectMove(validMoves);
    if (selectedMove != null) {
      if (!validMoves.contains(selectedMove))
        throw new IllegalArgumentException("Invalid move " + selectedMove);
    }
    return selectedMove;
  }

  public void publishMove(final Move move) {
    // Do nothing
  }

  protected abstract Move selectMove(final List<Move> validMoves);

  @Override
  public String toString() {
    return this.name + " (" + this.color + ")";
  }

  public abstract void abortCalculations();
}
