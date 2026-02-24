package com.sylvain.chess.play.players.interactive;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import java.util.Scanner;

/**
 * A player that informs its next move on the console.
 */
public class ConsolePlayer extends InteractivePlayer {
  private final Scanner scanner;

  public ConsolePlayer(final PlayerColor color, final String name, final ChessBoard board, final Scanner scanner) {
    super(color, name, board);
    this.scanner = scanner;
  }

  @Override
  protected String getNextMove() {
    System.out.print("Enter the next move for " + this + ": ");
    return this.scanner.nextLine();
  }
}
