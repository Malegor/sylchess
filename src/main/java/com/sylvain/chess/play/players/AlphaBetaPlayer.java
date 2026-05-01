package com.sylvain.chess.play.players;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.moves.EvaluatedMove;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.play.DrawConditions;
import com.sylvain.chess.play.GameStateInfo;
import com.sylvain.chess.play.Gameplay;
import lombok.extern.log4j.Log4j2;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A player calculating the best possible move, using the alpha-beta minimax algorithm.
 */
@Log4j2
public class AlphaBetaPlayer extends Player {
  private final static int EVALUATION_FOR_MATE = 500;

  private final DrawConditions drawConditions;
  private final int maxDepth;
  private final GameStateInfo info;

  public AlphaBetaPlayer(final PlayerColor color, final Gameplay game, final int maxNumberOfSemiMoves) {
    this(color, game.getBoard(), game.getInfo(), maxNumberOfSemiMoves, game.getDrawConditions());
  }

  public AlphaBetaPlayer(final PlayerColor color, final ChessBoard board, final GameStateInfo info, final int maxNumberOfSemiMoves,
                         final DrawConditions drawConditions) {
    super(color, AlphaBetaPlayer.class.getSimpleName(), board);
    this.maxDepth = maxNumberOfSemiMoves;
    this.info = info;
    this.drawConditions = drawConditions;
  }

  @Override
  protected Move selectMove(final List<Move> validMoves) {
    return this.selectEvaluatedMove(validMoves).move();
  }

  public EvaluatedMove selectEvaluatedMove(final List<Move> validMoves) {
    if (validMoves.size() == 1) {
      log.info("Forced move: {}", validMoves.getFirst());
    }
    // TODO: improve the following: this could not defend so well against perpetual check.
    // Problem: if one use the whole repeated positions structure, it could be very time and memory consuming.
    final Set<ChessBoard> twiceRepeatedPositions = this.info.getTwiceRepeatedPositions().stream().map(FenLoader::loadBoard).collect(Collectors.toSet());
    final EvaluatedMove move = alphaBeta(null, this.maxDepth, -EVALUATION_FOR_MATE - 1, EVALUATION_FOR_MATE + 1);
    final double evaluation = move.evaluation();
    log.info("Move eval: {}", this.isMateEvaluation(evaluation) ? this.getMateInN(evaluation) : evaluation);
    return move;
  }

  private String getMateInN(final double evaluation) {
    return "MATE IN " + this.getNumberOfMovesForMate(evaluation) + (evaluation < 0 ? " (opponent)" : "");
  }

  private int getNumberOfOpponentMovesAfter(final Move move) {
    return this.findAllOpponentMovesAfter(move).size();
  }

  private List<Move> findAllOpponentMovesAfter(final Move move) {
    move.simulate();
    final List<Move> moves = this.board.findAllValidMoves(ChessBoard.getOppositeColor(move.getColor()));
    move.rollback();
    return moves;
  }

  private EvaluatedMove alphaBeta(final Move move, final int depth, double alpha, double beta) {
    if (move != null)
      move.simulate();
    final Comparator<Move> byCheckingOpponent = (m1, m2) -> Boolean.compare(this.board.checksOppositeKing(m2.getDestinationPiece()),
            this.board.checksOppositeKing(m1.getDestinationPiece()));
    // TODO!! improve perf: attribute opponentMoves in EvaluateMove (+ previousMove) (SYLCHESS-99)
//    final Comparator<Move> byNumberOfOpponentResponses = (m1, m2) -> this.getNumberOfOpponentMovesAfter(m1) - this.getNumberOfOpponentMovesAfter(m2);
    final PlayerColor currentColor = move == null ? ChessBoard.getOppositeColor(this.color) : move.getColor();
    final PlayerColor oppositeColor = ChessBoard.getOppositeColor(currentColor);
    final Comparator<Move> moveOrderer = byCheckingOpponent
            .thenComparing(Move::getPromotionGain, Comparator.reverseOrder())
            .thenComparing(Move::getCapturedPieceValue, Comparator.reverseOrder())
            .thenComparing(Move::toString); // Arbitrary tie-breaker (for determinism) // OBS: doesn't fix it
    final List<Move> allValidMovesForOpponent = this.board.findAllValidMoves(oppositeColor).stream().sorted(moveOrderer).toList();
    if (depth <= 0 || allValidMovesForOpponent.isEmpty() || this.isConditionForStalemate()) {
      final double evaluation = depth > 0 && !allValidMovesForOpponent.isEmpty() ? 0
              : this.evaluateBoardFor(currentColor, allValidMovesForOpponent, this.maxDepth - depth);
      if (move != null)
        move.rollback();
      // TODO: avoid evaluating same position several times => map (position+color, eval) (SYLCHESS-56)
      return new EvaluatedMove(move, evaluation);
    }
    final boolean shouldMaximize = oppositeColor == this.color;
    final int multiplier = shouldMaximize ? 1 : -1;
    EvaluatedMove bestMoveForOpponent = new EvaluatedMove(null, - multiplier * (EVALUATION_FOR_MATE + 1));
    int index = 0;
    if (move == null)
      log.debug("Started search on {} possible moves...", allValidMovesForOpponent.size());
    for (final Move moveOpponent : allValidMovesForOpponent) {
      final EvaluatedMove nextMove = this.alphaBeta(moveOpponent, depth - 1, alpha, beta);
      if (move == null)
        log.debug("{}/{} - On {}, best response is: {}", index + 1, allValidMovesForOpponent.size(), moveOpponent, nextMove);
      if (multiplier * (nextMove.evaluation() - bestMoveForOpponent.evaluation()) > 0) {
        bestMoveForOpponent = new EvaluatedMove(moveOpponent, nextMove.evaluation());
      }
      if (multiplier * nextMove.evaluation() >= multiplier * (shouldMaximize ? beta : alpha)) { // alpha or beta cutoff
        if (move == null)
          log.debug("{}-{} - {}={} evaluation={} cutoff!", depth, move, shouldMaximize ? "beta" : "alpha", shouldMaximize ? beta : alpha, nextMove.evaluation());
        break;
      }
      if (shouldMaximize)
        alpha = Math.max(alpha, bestMoveForOpponent.evaluation());
      else
        beta = Math.min(beta, bestMoveForOpponent.evaluation());
      // If the best move for one player represents a mate in n moves, break in case the depth is too low
      if ((alpha > 0 && this.isMateEvaluation(alpha) && (this.getNumberOfMovesForMate(alpha) - 1) * 2 + 1 <= this.maxDepth - depth + 1) ||
              (beta < 0 && this.isMateEvaluation(beta) && (this.getNumberOfMovesForMate(beta) - 1) * 2 + 1 <= this.maxDepth - depth + 1)) {
        if (move == null)
          log.debug("{}-{} - Mate in {} cutoff!", depth, move, alpha > 0 ? this.getNumberOfMovesForMate(alpha) : this.getNumberOfMovesForMate(beta));
        break;
      }
      index++;
    }
    if (move != null)
      move.rollback();
    if (move == null)
      log.debug("alpha={} ; beta={}", alpha, beta);
    return bestMoveForOpponent;
  }

  private boolean isConditionForStalemate() {
    return false; // TODO
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
  private double evaluateBoardFor(final PlayerColor color, final List<Move> allValidMovesForOpponent, final int numberOfHalfMoves) {
    boolean shouldMaximize = color == this.color;
    int multiplier = shouldMaximize ? 1 : -1;
    final PlayerColor oppositeColor = ChessBoard.getOppositeColor(color);
    return this.board.isKingCheckMate(oppositeColor) ? multiplier * (EVALUATION_FOR_MATE - numberOfHalfMoves)
            : allValidMovesForOpponent.isEmpty() ? 0 : this.board.getPieces(this.color).values().stream().mapToDouble(PieceOnBoard::getDefaultValue).sum()
              - this.board.getPieces(ChessBoard.getOppositeColor(this.color)).values().stream().mapToDouble(PieceOnBoard::getDefaultValue).sum()
            + 1.0 / allValidMovesForOpponent.size();
  }
}
