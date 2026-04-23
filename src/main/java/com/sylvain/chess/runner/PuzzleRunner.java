package com.sylvain.chess.runner;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.play.GameStatus;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.players.MateSolver;
import com.sylvain.chess.play.players.Player;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Log4j2
public class PuzzleRunner {
  public static List<String> loadStringsFromFile(final String fileName) throws IOException {
    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream is = classloader.getResourceAsStream(fileName);
    final BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
    String line;
    final List<String> lines = new ArrayList<>();
    while ((line = reader.readLine()) != null) {
      lines.add(line);
    }
    return lines;
  }

  public static void main(String[] args) throws IOException {
    final String fileName = "fen/mate/mate3.fen";
    final List<String> fens = loadStringsFromFile(fileName);
    final long startTime = System.currentTimeMillis();
    for (final String fen : fens) {
      final Gameplay gameplay = FenLoader.loadPosition(fen);
      final List<Player> players = List.of(new MateSolver(PlayerColor.WHITE, gameplay.getBoard(), 7), new MateSolver(PlayerColor.BLACK, gameplay.getBoard(), 7));
      final GameStatus gameStatus = gameplay.playGame(players, gameplay.getInfo().getMoveNumber() + 3);
      assert !gameStatus.equals(GameStatus.PLAYING);
    }
    log.info("{} puzzles successfully solved in {} seconds", fens.size(), (System.currentTimeMillis() - startTime) / 1000);
  }
}
