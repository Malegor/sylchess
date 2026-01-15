package com.sylvain.chess.play.players;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;

import java.util.Comparator;
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
    final Color oppositeColor = ChessBoard.getOppositeColor(this.color);
    final Comparator<Move> checkComparator = (m1, m2) -> Boolean.compare(this.board.checksOppositeKing(m2.getDestinationPiece()),
            this.board.checksOppositeKing(m1.getDestinationPiece()));
    // TODO: thenCompare controlled squares around the king?
    if (this.maxNumberOfMoves <= 2) {
      for (final Move move1 : validMoves.stream().sorted(checkComparator).toList()) {
        move1.simulateForCheckValidate();
        if (this.board.isKingCheckMate(oppositeColor)) {
          move1.rollback();
          return move1;
        }
        boolean existDefendingMove = false;
        for (final Move move2 : this.board.findAllValidMoves(oppositeColor)) { // TODO: Use same comparator here with "this" king?
          move2.simulateForCheckValidate();
          boolean isDefendingMove = true;
          for (final Move move3 : this.board.findAllValidMoves(this.color).stream().sorted(checkComparator).toList()) {
            move3.simulateForCheckValidate();
            if (this.board.isKingCheckMate(oppositeColor)) {
              move3.rollback();
              isDefendingMove = false;
              break;
            }
            move3.rollback();
          }
          move2.rollback();
          if (isDefendingMove) {
            existDefendingMove = true;
            break;
          }
        }
        move1.rollback();
        if (!existDefendingMove)
          return move1;
      }
    }
    return null; // resign
  }
}
