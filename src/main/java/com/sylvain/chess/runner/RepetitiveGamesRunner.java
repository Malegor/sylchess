package com.sylvain.chess.runner;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenSaver;
import com.sylvain.chess.play.EndGame;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.players.AlphaBetaPlayer;
import com.sylvain.chess.play.players.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class RepetitiveGamesRunner {
  public static void main(String[] args) {
    // Data (could be arguments)
    final boolean byIndex = true;
    final int numberOfDifferentGames = 960;
    final int numberOfRepetitions = 1;
    ////
    final List<Long> gamesWhite = new ArrayList<>();
    final List<Long> gamesBlack = new ArrayList<>();
    final List<Long> gamesDraw = new ArrayList<>();
    final Map<String, List<Long>> sameEndgames = new HashMap<>(numberOfDifferentGames);
    final Random rand = new Random();
    long seed = 0;
    final long startTime = System.currentTimeMillis();
    for (int i = 1; i <= numberOfDifferentGames; i++) {
      if (!byIndex)
        seed = rand.nextLong();
      String commonFinalFen = null;
      EndGame endgame = null;
      final long gameDescription = byIndex ? i : seed;
      for (int j = 0; j < numberOfRepetitions; j++) {
        final ChessBoard board = byIndex ? ChessBoard.board960ByIndex(i) : ChessBoard.board960BySeed(seed);
        final Gameplay gameplay = new Gameplay(board);
        final List<Player> players = List.of(new AlphaBetaPlayer(PlayerColor.WHITE, gameplay,1), new AlphaBetaPlayer(PlayerColor.BLACK, gameplay,2));
        gameplay.playGame(players);
        final String fen = FenSaver.getPositionString(gameplay.getInfo(), board);
        if (commonFinalFen == null)
          commonFinalFen = fen;
        else if (!commonFinalFen.equals(fen)) {
          System.out.println(gamesWhite.size() + " / " + gamesDraw.size() + " / " + gamesBlack.size());
          throw new IllegalStateException("Indeterminism detected after " + (j+1) + " games for game=" + gameDescription);
        }
        endgame = gameplay.getEndGame();
        sameEndgames.putIfAbsent(commonFinalFen, new ArrayList<>(1));
        sameEndgames.get(commonFinalFen).add(gameDescription);
      }
      switch (endgame) {
        case WHITE_WINS -> gamesWhite.add(gameDescription);
        case BLACK_WINS -> gamesBlack.add(gameDescription);
        case DRAW -> gamesDraw.add(gameDescription);
        default -> throw new IllegalStateException("Unknown endgame: " + endgame);
      }
    }
    System.out.println("Number of games (w/d/b): " + gamesWhite.size() + " / " + gamesDraw.size() + " / " + gamesBlack.size());
    final EndGame majorWinner = gamesWhite.size() == gamesBlack.size() && gamesWhite.size() == gamesDraw.size() ? null :
            gamesWhite.size() >= gamesBlack.size() && gamesWhite.size() >= gamesDraw.size() ? EndGame.WHITE_WINS :
            gamesBlack.size() >= gamesWhite.size() && gamesBlack.size() >= gamesDraw.size() ? EndGame.BLACK_WINS :
                    EndGame.DRAW;
    System.out.println("Minority games: " + (!EndGame.WHITE_WINS.equals(majorWinner) ? gamesWhite + " " : "") +
            (!EndGame.DRAW.equals(majorWinner) ? gamesDraw + " " : "") + (!EndGame.BLACK_WINS.equals(majorWinner) ? gamesBlack : ""));
    if (sameEndgames.values().stream().mapToInt(List::size).sum() != numberOfDifferentGames)
      throw new IllegalStateException("Inconsistent endgames!");
    final List<Map.Entry<String, List<Long>>> sameEnds = sameEndgames.entrySet().stream().filter(e -> e.getValue().size() > 1).toList();
    if (!sameEnds.isEmpty())
      System.out.println("Games leading to the same endgame: " + sameEnds);
    System.out.println(numberOfDifferentGames * numberOfRepetitions + " games played in " + (System.currentTimeMillis() - startTime) + " ms");
  }
}
