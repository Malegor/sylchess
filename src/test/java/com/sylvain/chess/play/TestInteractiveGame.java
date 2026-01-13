package com.sylvain.chess.play;

import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Scanner;

public class TestInteractiveGame {
  @Test
  public void testFastMate() {
    final String simulatedMoves = "f3\ne6\ng4\nQh4";
    final InputStream mockInput = new ByteArrayInputStream(simulatedMoves.getBytes(StandardCharsets.UTF_8));
    final EndGame endGame = InteractiveGame.play(new Scanner(mockInput), List.of("a", "b"));
    Assert.assertEquals(EndGame.BLACK_WINS, endGame);
  }

  @Test
  public void testDraw() {
    final String simulatedMoves = "Nf3\nNc6\nNg1\nNb8\nNf3\nNc6\nNg1\nNb8\nNf3\nNc6\nNg1\nNb8";
    final InputStream mockInput = new ByteArrayInputStream(simulatedMoves.getBytes(StandardCharsets.UTF_8));
    final EndGame endGame = InteractiveGame.play(new Scanner(mockInput), List.of("a", "b"));
    Assert.assertEquals(EndGame.DRAW, endGame);
  }
}
