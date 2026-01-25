package com.sylvain.chess.play.players;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.EvaluatedMove;
import com.sylvain.chess.moves.Move;
import lombok.extern.log4j.Log4j2;

import java.util.Comparator;
import java.util.List;

/**
 * A player dedicated to solve mates in n moves, exploring all possible moves for both sides, in breadth-first search(?)
 * May be easier for a first version DFS up to a maximum number of moves.
 */
@Log4j2
public class MateSolver extends Player {
  private final static int MAX_VALUE = 500;

  private final int maxNumberOfMoves;

  public MateSolver(final Color color, final ChessBoard board, final int maxNumberOfMoves) {
    super(color, "MateSolver", board);
    this.maxNumberOfMoves = maxNumberOfMoves;
  }

  @Override
  protected Move selectMove(final List<Move> validMoves) {
    final EvaluatedMove move = alphaBeta(null, this.maxNumberOfMoves * 2 - 1, - MAX_VALUE, MAX_VALUE);
    log.info("Move eval: {}", move.getEvaluation());
    return move.getMove();
  }

  private EvaluatedMove alphaBeta(final Move move, final int depth, double alpha, double beta) {
    if (move != null)
      move.simulate();
    final Comparator<Move> checkComparator = (m1, m2) -> Boolean.compare(this.board.checksOppositeKing(m2.getDestinationPiece()),
            this.board.checksOppositeKing(m1.getDestinationPiece()));
    final Color currentColor = move == null ? ChessBoard.getOppositeColor(this.color) : move.getColor();
    final Color oppositeColor = ChessBoard.getOppositeColor(currentColor);
    final List<Move> allValidMovesForOpponent = this.board.findAllValidMoves(oppositeColor).stream().sorted(checkComparator).toList();
    if (depth == 0 || allValidMovesForOpponent.isEmpty()) {
      final int evaluation = this.evaluateBoardFor(currentColor, allValidMovesForOpponent);
      if (move != null)
        move.rollback();
      // TODO: avoid evaluating same position several times => map (position+color, eval)
      return new EvaluatedMove(move, evaluation);
    }
    boolean shouldMaximize = oppositeColor == this.color;
    int multiplier = shouldMaximize ? 1 : -1;
    EvaluatedMove bestMoveForOpponent = new EvaluatedMove(null, - multiplier * MAX_VALUE);
    for (final Move moveOpponent : allValidMovesForOpponent) {
      final EvaluatedMove nextMove = this.alphaBeta(moveOpponent, depth - 1, alpha, beta);
      if (multiplier * (nextMove.getEvaluation() - bestMoveForOpponent.getEvaluation()) > 0) {
        bestMoveForOpponent = new EvaluatedMove(moveOpponent, nextMove.getEvaluation());
      }
      if (multiplier * nextMove.getEvaluation() >= (shouldMaximize ? beta : multiplier * alpha)) // alpha or beta cutoff
        break;
      if (shouldMaximize)
        alpha = Math.max(alpha, bestMoveForOpponent.getEvaluation());
      else
        beta = Math.min(beta, bestMoveForOpponent.getEvaluation());
    }
    if (move != null)
      move.rollback();
    return bestMoveForOpponent;
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
    // TODO: more complete evaluation: count pieces "values" etc. (this eval only works for puzzles of kind checkmate in n moves)
    return 0;
  }
}
