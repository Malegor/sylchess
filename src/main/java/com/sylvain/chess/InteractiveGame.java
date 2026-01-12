package com.sylvain.chess;

import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.InteractivePlayer;

import java.io.InputStream;
import java.util.List;
import java.util.Scanner;

public class InteractiveGame {
  public static void main(String[] args) {
    final InputStream inputStream = System.in; // TODO: param for tests
    final Scanner scanner = new Scanner(inputStream);
    System.out.print("Enter your name: ");
    final String name = scanner.nextLine();
    System.out.println("Hello, " + name + "! You will play with WHITE against a dummy BLACK player.");
    final ChessBoard board = ChessBoard.defaultBoard();
    final Gameplay game = new Gameplay(board, List.of(new InteractivePlayer(Color.WHITE, name, board, scanner), new DummyPlayer(Color.BLACK, board)));
    game.playGame();
    System.out.println(game.getEndGame().getPgn());
    scanner.close();
  }
}
