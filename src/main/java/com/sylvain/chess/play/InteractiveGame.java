package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.InteractivePlayer;
import com.sylvain.chess.play.players.Player;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class InteractiveGame {
  public static void main(String[] args) {
    final InputStream inputStream = System.in; // TODO: param for tests
    final Scanner scanner = new Scanner(inputStream);
    final List<String> names = new ArrayList<>(2);
    for (final Color color : Color.values()) {
      System.out.print("Enter name for " + color + " (leave empty to play a dummy player): ");
      names.add(scanner.nextLine());
    }
    play(scanner, names);
    scanner.close();
  }

  public static EndGame play(final Scanner scanner, final List<String> playerNames) {
    final ChessBoard board = ChessBoard.defaultBoard();
    final Player whitePlayer = playerNames.getFirst().isEmpty() ? new DummyPlayer(Color.WHITE, board) : new InteractivePlayer(Color.WHITE, playerNames.getFirst(), board, scanner);
    final Player blackPlayer = playerNames.getLast().isEmpty() ? new DummyPlayer(Color.BLACK, board) : new InteractivePlayer(Color.BLACK, playerNames.getLast(), board, scanner);
    final Gameplay game = new Gameplay(board);
    game.playGame(List.of(whitePlayer, blackPlayer));
    System.out.println(game.getEndGame().getPgn());
    return game.getEndGame();
  }
}
