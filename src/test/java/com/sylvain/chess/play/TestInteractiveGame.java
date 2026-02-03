package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.TestLoadPosition;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.play.players.InteractivePlayer;
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
    final List<Player> players = List.of(new InteractivePlayer(Color.WHITE, "white", board, scanner), new InteractivePlayer(Color.BLACK, "black", board, scanner));
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
    final List<Player> players = List.of(new InteractivePlayer(Color.WHITE, "white", board, scanner), new InteractivePlayer(Color.BLACK, "black", board, scanner));
    final EndGame endGame = InteractiveGame.play(new Gameplay(board), players);
    scanner.close();
    Assert.assertEquals(EndGame.DRAW, endGame);
  }

  @Test
  public void testPuzzle() throws IOException {
    final String simulatedWhiteMoves = "Kg2\nKg3"; // OBS: only for white player
    final InputStream mockInput = new ByteArrayInputStream(simulatedWhiteMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final Gameplay game = TestLoadPosition.loadPositionFromFile("fen/mate3-3.fen");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new InteractivePlayer(Color.WHITE, "white", game.getBoard(), scanner), new MateSolver(Color.BLACK, game.getBoard(), 3));
    final EndGame endGame = InteractiveGame.play(game, players);
    scanner.close();
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }

  /**
   * OBS: several problems for this test:
   * 1- Inconsistency on pieces that are already on the board --> solved!
   * 2- terminates in a draw (repeated position), in spite of the whites always finding a mate in 5 (the problem is, they don't find the quickest mate)
   * 3- for a few moves, it lasts about 1 hour
   * 4- The solver doesn't consider the drawing end games (3 times same position etc.) -> write unit tests
   * @throws IOException - Exception thrown from reading a non-existing fen file.
   */
  @Test
  @Ignore // Because of the processing time, soon to be fixed
  public void testPuzzleInconsistency() throws IOException {
    final String simulatedBlackMoves = "Re6\nRxc5\nRc2\nNd2\nNxb4\nKh8\nKg8\nh5\nKh7\ng5\nKg8\ng4\nKh7\nKg8\nKh7\nKg8";
    final InputStream mockInput = new ByteArrayInputStream(simulatedBlackMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final Gameplay game = TestLoadPosition.loadPositionFromFile("fen/mate2-5.fen");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new MateSolver(Color.WHITE, game.getBoard(), 5), new InteractivePlayer(Color.BLACK, "black", game.getBoard(), scanner));
    final EndGame endGame = InteractiveGame.play(game, players);
    scanner.close();
    Assert.assertEquals(EndGame.WHITE_WINS, endGame);
  }

  @Test
  @Ignore // Bug of the mate in 5 (the solver doesn't choose the fastest mate and ends up playing repeated moves)
  public void testMateIn5() {
    final String simulatedBlackMoves = "Kh7\nKg8\nKh7\nKg8";
    final InputStream mockInput = new ByteArrayInputStream(simulatedBlackMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final Gameplay game = FenLoader.loadPosition("6k1/pp2R1B1/8/1P2pB1p/6pP/8/3K3P/8 w - - 0 1");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new MateSolver(Color.WHITE, game.getBoard(), 5), new InteractivePlayer(Color.BLACK, "black", game.getBoard(), scanner));
    final EndGame endGame = InteractiveGame.play(game, players);
    scanner.close();
    Assert.assertEquals(EndGame.WHITE_WINS, endGame);
  }
}
