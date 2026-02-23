package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.TestLoadPosition;
import com.sylvain.chess.play.players.ConsolePlayer;
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

public class TestInteractiveGame {
  @Test
  public void testMatDeLimbecile() {
    final String simulatedMoves = "f3\ne5\ng4\nQh4";
    final InputStream mockInput = new ByteArrayInputStream(simulatedMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final ChessBoard board = ChessBoard.defaultBoard();
    final List<Player> players = List.of(new ConsolePlayer(Color.WHITE, "white", board, scanner), new ConsolePlayer(Color.BLACK, "black", board, scanner));
    final EndGame endGame = InteractiveGame.play(new Gameplay(board), players);
    scanner.close();
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }

  @Test
  public void testRepeatedPositions() {
    final String simulatedMoves = "Nf3\nNc6\nNg1\nNb8\nNf3\nNc6\nNg1\nNb8\nNf3\nNc6\nNg1\nNb8";
    final InputStream mockInput = new ByteArrayInputStream(simulatedMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final ChessBoard board = ChessBoard.defaultBoard();
    final List<Player> players = List.of(new ConsolePlayer(Color.WHITE, "white", board, scanner), new ConsolePlayer(Color.BLACK, "black", board, scanner));
    final EndGame endGame = InteractiveGame.play(new Gameplay(board), players);
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
    final List<Player> players = List.of(new ConsolePlayer(Color.WHITE, "white", game.getBoard(), scanner), new MateSolver(Color.BLACK, game.getBoard(), 3));
    final EndGame endGame = InteractiveGame.play(game, players);
    scanner.close();
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }

  @Test
  public void testMateIn3BothSolvers() throws IOException {
    final Gameplay game = TestLoadPosition.loadPositionFromFile("fen/mate3-3.fen");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new MateSolver(Color.WHITE, game.getBoard(), 3), new MateSolver(Color.BLACK, game.getBoard(), 3));
    final EndGame endGame = InteractiveGame.play(game, players);
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }

  /**
   * OBS: several problems for this test:
   * 1- Inconsistency on pieces that are already on the board --> solved!
   * 2- terminates in a draw (repeated position), in spite of the whites always finding a mate in 5 (the problem is, they don't find the quickest mate) --> solved!
   * 3- for a few moves, it lasts about 1 hour --> "half-solved" (now: less than 10 minutes for all the 7 semi-moves, with a solver attacking and a solver defending)
   * 4- The solver doesn't consider the drawing end games (3 times same position etc.) -> write unit tests
   * @throws IOException - Exception thrown from reading a non-existing fen file.
   */
  @Test
  @Ignore // Because of the processing time, soon to be fixed
  public void testPuzzleProcessingTime() throws IOException {
    final Gameplay game = TestLoadPosition.loadPositionFromFile("fen/mate4-8.fen");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new MateSolver(Color.WHITE, game.getBoard(), 5), new MateSolver(Color.BLACK, game.getBoard(), 5));
    final EndGame endGame = InteractiveGame.play(game, players);
    Assert.assertEquals(EndGame.WHITE_WINS, endGame);
  }
}
