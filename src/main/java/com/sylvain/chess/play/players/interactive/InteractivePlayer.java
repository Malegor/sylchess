package com.sylvain.chess.play.players.interactive;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.play.players.Player;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;

@Log4j2
public abstract class InteractivePlayer extends Player {
  public static final int MAX_ATTEMPTS = 5; // TODO abstract

  public InteractivePlayer(final PlayerColor color, final String name, final ChessBoard board) {
    super(color, name, board);
  }

  @Override
  protected Move selectMove(List<Move> validMoves) {
    int count = 0;
    while (count++ < MAX_ATTEMPTS) {
      // TODO: allow resigning
      final String moveStr = this.getNextMove();
      final Optional<Move> optMove = validMoves.stream().filter(m -> m.toPgn().equals(moveStr)).findFirst();
      if (optMove.isPresent())
        return optMove.get();
      else
        this.handleInvalidMove(validMoves, moveStr);
    }
    log.info("No valid move selected after " + MAX_ATTEMPTS + " attempts, game considered resigned.");
    return null;
  }

  protected void handleInvalidMove(final List<Move> validMoves, final String moveStr) {
    log.info("Invalid move {}. Try again.", moveStr);
    log.info("Valid moves: {}", validMoves.stream().map(Move::toPgn).toList());
  }

  protected abstract String getNextMove();
}
