package com.sylvain.chess.play;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.TestLoadPosition;
import com.sylvain.chess.play.players.interactive.ConsolePlayer;
import com.sylvain.chess.play.players.MateSolver;
import com.sylvain.chess.play.players.Player;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class TestConsoleInteractiveGame {
  @Test
  public void testMatDeLimbecile() {
    final String simulatedMoves = "f3\ne5\ng4\nQh4";
    final InputStream mockInput = new ByteArrayInputStream(simulatedMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final ChessBoard board = ChessBoard.defaultBoard();
    final List<Player> players = List.of(new ConsolePlayer(PlayerColor.WHITE, "white", board, scanner), new ConsolePlayer(PlayerColor.BLACK, "black", board, scanner));
    final EndGame endGame = ConsoleInteractiveGame.play(new Gameplay(board), players);
    scanner.close();
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }

  @Test
  public void testRepeatedPositions() {
    final String simulatedMoves = "Nf3\nNc6\nNg1\nNb8\nNf3\nNc6\nNg1\nNb8\nNf3\nNc6\nNg1\nNb8";
    final InputStream mockInput = new ByteArrayInputStream(simulatedMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final ChessBoard board = ChessBoard.defaultBoard();
    final List<Player> players = List.of(new ConsolePlayer(PlayerColor.WHITE, "white", board, scanner), new ConsolePlayer(PlayerColor.BLACK, "black", board, scanner));
    final EndGame endGame = ConsoleInteractiveGame.play(new Gameplay(board), players);
    scanner.close();
    Assert.assertEquals(EndGame.DRAW, endGame);
  }

  @Test
  public void testMateIn3() throws IOException {
    final String simulatedWhiteMoves = "Kg2\nKg3"; // OBS: only for white player
    final InputStream mockInput = new ByteArrayInputStream(simulatedWhiteMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final Gameplay game = TestLoadPosition.loadPositionFromFile("fen/mate3-3.fen");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new ConsolePlayer(PlayerColor.WHITE, "white", game.getBoard(), scanner), new MateSolver(PlayerColor.BLACK, game.getBoard(), 5));
    final EndGame endGame = ConsoleInteractiveGame.play(game, players);
    scanner.close();
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }

  @Test
  public void testMateIn3BothSolvers() throws IOException {
    final Gameplay game = TestLoadPosition.loadPositionFromFile("fen/mate3-3.fen");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new MateSolver(PlayerColor.WHITE, game.getBoard(), 5), new MateSolver(PlayerColor.BLACK, game.getBoard(), 5));
    final EndGame endGame = ConsoleInteractiveGame.play(game, players);
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }

  /**
   * OBS: several problems for this test:
   * 1- Inconsistency on pieces that are already on the board --> solved!
   * 2- terminates in a draw (repeated position), in spite of the whites always finding a mate in 5 (the problem is, they don't find the quickest mate) --> solved!
   * 3- for a few moves, it lasts about 1 hour --> "half-solved" (now: less than 10 minutes for all the 7 semi-moves, with a solver attacking and a solver defending) --> solved!
   * 4- The solver doesn't consider the drawing end games (3 times same position etc.) -> write unit tests
   * @throws IOException - Exception thrown from reading a non-existing fen file.
   */
  @Test
  public void testPuzzleProcessingTime() throws IOException {
    final Gameplay game = TestLoadPosition.loadPositionFromFile("fen/mate4-8.fen");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new MateSolver(PlayerColor.WHITE, game.getBoard(), 6), new MateSolver(PlayerColor.BLACK, game.getBoard(), 6));
    final EndGame endGame = ConsoleInteractiveGame.play(game, players);
    Assert.assertEquals(EndGame.WHITE_WINS, endGame);
  }
}
