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
  private final static int EVALUATION_FOR_MATE = 500;

  private final int maxDepth;

  public MateSolver(final Color color, final ChessBoard board, final int maxNumberOfMoves) {
    super(color, "MateSolver", board);
    this.maxDepth = maxNumberOfMoves * 2 - 1;
  }

  @Override
  protected Move selectMove(final List<Move> validMoves) {
    final EvaluatedMove move = alphaBeta(null, this.maxDepth, -EVALUATION_FOR_MATE - 1, EVALUATION_FOR_MATE + 1);
    final double evaluation = move.getEvaluation();
    log.info("Move eval: {}", this.isMateEvaluation(evaluation) ?
            "MATE IN " + this.getNumberOfMovesForMate(evaluation) + (evaluation < 0 ? " (opponent)" : "") :
            evaluation);
    return move.getMove();
  }

  private EvaluatedMove alphaBeta(final Move move, final int depth, double alpha, double beta) {
    if (move != null)
      move.simulate();
    final Comparator<Move> byCheckingOpponent = (m1, m2) -> Boolean.compare(this.board.checksOppositeKing(m2.getDestinationPiece()),
            this.board.checksOppositeKing(m1.getDestinationPiece()));
    final Color currentColor = move == null ? ChessBoard.getOppositeColor(this.color) : move.getColor();
    final Color oppositeColor = ChessBoard.getOppositeColor(currentColor);
    final List<Move> allValidMovesForOpponent = this.board.findAllValidMoves(oppositeColor).stream().sorted(byCheckingOpponent).toList();
    if (depth <= 0 || allValidMovesForOpponent.isEmpty()) {
      final int evaluation = this.evaluateBoardFor(currentColor, allValidMovesForOpponent, this.maxDepth - depth);
      if (move != null)
        move.rollback();
      // TODO: avoid evaluating same position several times => map (position+color, eval)
      return new EvaluatedMove(move, evaluation);
    }
    final boolean shouldMaximize = oppositeColor == this.color;
    final int multiplier = shouldMaximize ? 1 : -1;
    EvaluatedMove bestMoveForOpponent = new EvaluatedMove(null, - multiplier * (EVALUATION_FOR_MATE + 1));
    for (final Move moveOpponent : allValidMovesForOpponent) {
      final EvaluatedMove nextMove = this.alphaBeta(moveOpponent, depth - 1, alpha, beta);
      if (multiplier * (nextMove.getEvaluation() - bestMoveForOpponent.getEvaluation()) > 0) {
        bestMoveForOpponent = new EvaluatedMove(moveOpponent, nextMove.getEvaluation());
      }
      if (multiplier * nextMove.getEvaluation() >= multiplier * (shouldMaximize ? beta : alpha)) // alpha or beta cutoff
        break;
      if (shouldMaximize)
        alpha = Math.max(alpha, bestMoveForOpponent.getEvaluation());
      else
        beta = Math.min(beta, bestMoveForOpponent.getEvaluation());
      // If the best move for one player represents a mate in n moves, break in case the depth is too low
      if ((alpha > 0 && this.isMateEvaluation(alpha) && (this.getNumberOfMovesForMate(alpha) - 1) * 2 + 1 <= this.maxDepth - depth + 1) ||
              (beta < 0 && this.isMateEvaluation(beta) && (this.getNumberOfMovesForMate(beta) - 1) * 2 + 1 <= this.maxDepth - depth + 1))
        break;
    }
    if (move != null)
      move.rollback();
    if (move == null)
      log.debug("alpha={} ; beta={}", alpha, beta);
    return bestMoveForOpponent;
  }

  private boolean isMateEvaluation(final double evaluation) {
    return Math.abs(evaluation) > EVALUATION_FOR_MATE - 50 && Math.abs(evaluation) < EVALUATION_FOR_MATE;
  }

  private int getNumberOfMovesForMate(final double evaluation) {
    return (EVALUATION_FOR_MATE - (int) Math.abs(evaluation) + 1) / 2;
  }

  /**
   *
   * @param color                    - The color that has just played its move
   * @param allValidMovesForOpponent - All the possible moves for the opponent
   * @param numberOfHalfMoves - The number of half moves since the beginning of the search
   * @return The evaluation of the position after the move.
   */
  private int evaluateBoardFor(final Color color, final List<Move> allValidMovesForOpponent, final int numberOfHalfMoves) {
    boolean shouldMaximize = color == this.color;
    int multiplier = shouldMaximize ? 1 : -1;
    final Color oppositeColor = ChessBoard.getOppositeColor(color);
    if (this.board.isKingCheckMate(oppositeColor)) {
      return multiplier * (EVALUATION_FOR_MATE - numberOfHalfMoves);
    }
    // TODO: more complete evaluation: count pieces "values" etc. (this eval only works for puzzles of kind checkmate in n moves)
    return 0;
  }
}
