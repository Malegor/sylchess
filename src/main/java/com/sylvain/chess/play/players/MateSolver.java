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
  private final static int MAX_VALUE = 500;

  private final int maxNumberOfMoves;

  public MateSolver(final Color color, final ChessBoard board, final int maxNumberOfMoves) {
    super(color, "MateSolver", board);
    this.maxNumberOfMoves = maxNumberOfMoves;
  }

  @Override
  protected Move selectMove(final List<Move> validMoves) {
    final int value = alphabeta(null, this.maxNumberOfMoves * 2 - 1, - MAX_VALUE, MAX_VALUE);
    System.out.println(value);
    return null;
    /*
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
     */
  }

  private int alphabeta(final Move move, final int depth, int alpha, int beta) { // TODO make alpha & beta NOT parameters(?)
    if (move != null)
      move.simulateForCheckValidate();
    final Comparator<Move> checkComparator = (m1, m2) -> Boolean.compare(this.board.checksOppositeKing(m2.getDestinationPiece()),
            this.board.checksOppositeKing(m1.getDestinationPiece()));
    final Color currentColor = move == null ? ChessBoard.getOppositeColor(this.color) : move.getColor();
    final Color oppositeColor = ChessBoard.getOppositeColor(currentColor);
    final List<Move> allValidMovesForOpponent = this.board.findAllValidMoves(oppositeColor).stream().sorted(checkComparator).toList(); // TODO: order moves by checking king
    if (depth == 0 || allValidMovesForOpponent.isEmpty()) {
      final int evaluation = this.evaluateBoardFor(currentColor, allValidMovesForOpponent);
      if (move != null)
        move.rollback();
      // TODO: return move+evaluation --> new attribute evaluation in Move?
      // TODO: avoid evaluating same position several times => map (position+color, eval)
      return evaluation;
    }
    boolean shouldMaximize = oppositeColor == this.color;
    int multiplier = shouldMaximize ? 1 : -1;
    int bestValue = - multiplier * MAX_VALUE;
    for (final Move moveOpponent : allValidMovesForOpponent) {
      bestValue = multiplier * Math.max(multiplier * bestValue, multiplier * alphabeta(moveOpponent, depth - 1, alpha, beta));
      if (multiplier * bestValue >= (shouldMaximize ? beta : multiplier * alpha)) // alpha or beta cutoff
        break;
    }
    if (shouldMaximize) {
      alpha = Math.max(alpha, bestValue);
    }
    else
      beta = Math.min(beta, bestValue);
    if (move != null)
      move.rollback();
    return bestValue;
  }

  /**
   *
   * @param color - The color that has just played its move
   * @param allValidMovesForOpponent - All the possible moves for the opponent
   * @return The evaluation of the position after the move.
   */
  private int evaluateBoardFor(final Color color, final List<Move> allValidMovesForOpponent) {
    boolean shouldMaximize = color == this.color;
    int multiplier = shouldMaximize ? 1 : -1;
    final Color oppositeColor = ChessBoard.getOppositeColor(color);
    if (this.board.isKingCheckMate(oppositeColor)) {
      return multiplier * MAX_VALUE;
    }
    // TODO: more complete evaluation: count pieces "values" etc.
    return 0;
  }
}
