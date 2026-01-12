package com.sylvain.chess.play.players;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;

import java.util.List;
import java.util.Optional;
import java.util.Scanner;

/**
 * A player that informs its next move on the console.
 */
public class InteractivePlayer extends Player {
  private final Scanner scanner;

  public InteractivePlayer(final Color color, final String name, final ChessBoard board, final Scanner scanner) {
    super(color, name, board);
    this.scanner = scanner;
  }

  @Override
  protected Move selectMove(List<Move> validMoves) {
    int count = 0;
    while (count++ < 3) {
      System.out.print("Enter your next move: ");
      // TODO: allow resigning
      final String moveStr = this.scanner.nextLine();
      final Optional<Move> move = validMoves.stream().filter(m -> m.toPgn().equals(moveStr)).findFirst();
      if (move.isPresent())
        return move.get();
      else {
        System.out.println("Invalid move. Try again.");
        System.out.println("Valid moves: " + validMoves.stream().map(Move::toPgn).toList());
      }
    }
    System.out.println("No valid move selected after 3 attempts, game considered resigned.");
    return null;
  }
}
