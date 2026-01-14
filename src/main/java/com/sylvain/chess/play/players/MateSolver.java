package com.sylvain.chess.play.players;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;

import java.util.List;

/**
 * A player dedicated to solve mates in n moves, exploring all possible moves for both sides, in breadth-first search(?)
 * May be easier for a first version DFS up to a maximum number of moves.
 */
public class MateSolver extends Player {
  private final int maxNumberOfMoves;

  public MateSolver(final Color color, final ChessBoard board, final int maxNumberOfMoves) {
    super(color, "MateSolver", board);
    this.maxNumberOfMoves = maxNumberOfMoves;
  }

  @Override
  protected Move selectMove(final List<Move> validMoves) {
    if (this.maxNumberOfMoves == 1) {
      for (final Move move : validMoves) {
        move.simulateForCheckValidate();
        if (this.board.isKingCheckMate(ChessBoard.getOppositeColor(move.getColor()))) {
          move.rollback();
          return move;
        }
        move.rollback();
      }
    }
    return null; // resign
  }
}
