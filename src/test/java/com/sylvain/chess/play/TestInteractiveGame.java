package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.TestLoadPosition;
import com.sylvain.chess.play.players.InteractivePlayer;
import com.sylvain.chess.play.players.MateSolver;
import com.sylvain.chess.play.players.Player;
import org.junit.Assert;
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
    final String simulatedMoves = "Kg2\nKg3"; // OBS: only for white player
    final InputStream mockInput = new ByteArrayInputStream(simulatedMoves.getBytes(StandardCharsets.UTF_8));
    final Scanner scanner = new Scanner(mockInput);
    final Gameplay game = TestLoadPosition.loadPositionFromFile("fen/mate3-3.fen");
    game.getBoard().printBoard();
    final List<Player> players = List.of(new InteractivePlayer(Color.WHITE, "white", game.getBoard(), scanner), new MateSolver(Color.BLACK, game.getBoard(), 3));
    final EndGame endGame = InteractiveGame.play(game, players);
    scanner.close();
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }
}
