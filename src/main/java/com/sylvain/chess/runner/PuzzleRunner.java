package com.sylvain.chess.runner;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.play.GameStatus;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.players.AlphaBetaPlayer;
import com.sylvain.chess.play.players.Player;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    final int numberOfMovesForMate = 3;
    final String fileName = "fen/mate/mate" + numberOfMovesForMate + ".fen";
    final int depth = 7;
    final List<String> fens = loadStringsFromFile(fileName);
    final long startTime = System.currentTimeMillis();
    long maxTime = 0;
    for (final String fen : fens) {
      long specificStartTime = System.currentTimeMillis();
      final Gameplay gameplay = FenLoader.loadPosition(fen);
      final List<Player> players = List.of(new AlphaBetaPlayer(PlayerColor.WHITE, gameplay.getBoard(), gameplay.getInfo(), depth, gameplay.getDrawConditions()),
              new AlphaBetaPlayer(PlayerColor.BLACK, gameplay.getBoard(), gameplay.getInfo(), depth, gameplay.getDrawConditions()));
      final GameStatus gameStatus = gameplay.playGame(players,
              gameplay.getInfo().getMoveNumber() + numberOfMovesForMate - (gameplay.getHistory().getFirstPlayingColor().equals(PlayerColor.BLACK) ? 0 : 1));
      if (gameStatus.equals(GameStatus.ABORTED_MAX_NB_OF_MOVES))
        throw new IllegalStateException("Game status should not be PLAYING: " + fen);
      maxTime = Math.max(System.currentTimeMillis() - specificStartTime, maxTime);
    }
    final long totalTime = (System.currentTimeMillis() - startTime) / 1000;
    log.info("{} puzzles successfully solved in {} seconds, max(ms)={}", fens.size(), totalTime, maxTime);
    final String version = "1.0-SNAPSHOT";
    // final String branch; // TODO
    log.info("{},{},{},{},{},{},{}", getCurrentDate(), version, Paths.get(fileName).getFileName().toString(), fens.size(), depth, totalTime, maxTime);
  }

  private static String getCurrentDate() {
    final LocalDate today = LocalDate.now();
    final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
    return today.format(formatter);
  }
}
