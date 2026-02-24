package com.sylvain.chess.play.players.interactive;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.play.players.Player;

import java.util.List;
import java.util.Optional;

public abstract class InteractivePlayer extends Player {
  public InteractivePlayer(final PlayerColor color, final String name, final ChessBoard board) {
    super(color, name, board);
  }

  @Override
  protected Move selectMove(List<Move> validMoves) {
    int count = 0;
    while (count++ < 3) { // TODO abstract
      // TODO: allow resigning
      final String moveStr = this.getNextMove();
      final Optional<Move> move = validMoves.stream().filter(m -> m.toPgn().equals(moveStr)).findFirst();
      if (move.isPresent())
        return move.get();
      else {
        System.out.println("Invalid move " + moveStr + ". Try again.");// TODO: abstract handleInvalidMove
        System.out.println("Valid moves: " + validMoves.stream().map(Move::toPgn).toList());
      }
    }
    System.out.println("No valid move selected after 3 attempts, game considered resigned.");
    return null;
  }

  protected abstract String getNextMove();
}
