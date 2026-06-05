package com.sylvain.chess.runner;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenSaver;
import com.sylvain.chess.play.EndGame;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.AlphaBetaPlayer;
import com.sylvain.chess.play.players.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class DeterminismRunner {
  public static void main(String[] args) {
    // Data
    final boolean byIndex = true;
    final int numberOfDifferentGames = 960;
    final int numberOfRepetitions = 1;
    ////
    final List<Long> gamesWithoutWinning = new ArrayList<>();
    final Random rand = new Random();
    int whiteWins = 0;
    int blackWins = 0;
    int draws = 0;
    long seed = 0;
    final long startTime = System.currentTimeMillis();
    for (int i = 1; i <= numberOfDifferentGames; i++) {
      if (!byIndex)
        seed = rand.nextLong();
      String commonFinalFen = null;
      EndGame endgame = null;
      for (int j = 0; j < numberOfRepetitions; j++) {
        final ChessBoard board = byIndex ? ChessBoard.get960BoardByIndex(i) : ChessBoard.get960BoardBySeed(seed);
        final Gameplay gameplay = new Gameplay(board);
        final List<Player> players = List.of(new DummyPlayer(PlayerColor.WHITE, board), new AlphaBetaPlayer(PlayerColor.BLACK, board, gameplay.getInfo(),
                3, gameplay.getDrawConditions()));
        gameplay.playGame(players);
        final String fen = FenSaver.getPositionString(gameplay);
        if (commonFinalFen == null) {
          commonFinalFen = fen;
        }
        else if (!commonFinalFen.equals(fen)) {
          System.out.println(whiteWins + " / " + draws + " / " + blackWins);
          throw new IllegalStateException("Indeterminism detected after " + (j+1) + " games for game=" + (byIndex ? i : seed));
        }
        endgame = gameplay.getEndGame();
        if (!EndGame.BLACK_WINS.equals(endgame) && (gamesWithoutWinning.isEmpty() || !gamesWithoutWinning.getLast().equals((byIndex ? i : seed))))
          gamesWithoutWinning.add((byIndex ? i : seed));
      }
      switch (endgame) {
        case WHITE_WINS -> whiteWins += 1;
        case BLACK_WINS -> blackWins += 1;
        case DRAW -> draws += 1;
        default -> throw new IllegalStateException("Unknown endgame: " + endgame);
      }
    }
    System.out.println("Number of games (w/d/b): " + whiteWins + " / " + draws + " / " + blackWins);
    System.out.println("Unvictorious games: " + gamesWithoutWinning);
    System.out.println(numberOfDifferentGames * numberOfRepetitions + " games played in " + (System.currentTimeMillis() - startTime) + " ms");
  }
}
