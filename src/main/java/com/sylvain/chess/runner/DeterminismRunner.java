package com.sylvain.chess.runner;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenSaver;
import com.sylvain.chess.play.EndGame;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.MateSolver;
import com.sylvain.chess.play.players.Player;

import java.util.List;
import java.util.Random;

public class DeterminismRunner {
  public static void main(String[] args) {
    final int numberOfDifferentGames = 100;
    int numberOfRepetitions = 1;
    final Random rand = new Random();
    int whiteWins = 0;
    int blackWins = 0;
    int draws = 0;
    final long startTime = System.currentTimeMillis();
    for (int i = 0; i < numberOfDifferentGames; i++) {
      final long seed = rand.nextLong();
      String commonFinalFen = null;
      EndGame endgame = null;
      for (int j = 0; j < numberOfRepetitions; j++) {
        final ChessBoard board = ChessBoard.get960Board(seed);
        final Gameplay gameplay = new Gameplay(board);
        final List<Player> players = List.of(new DummyPlayer(PlayerColor.WHITE, board), new MateSolver(PlayerColor.BLACK, board, 3));
        gameplay.playGame(players);
        final String fen = FenSaver.getPositionString(gameplay);
        if (commonFinalFen == null) {
          commonFinalFen = fen;
        }
        else if (!commonFinalFen.equals(fen)) {
          System.out.println(whiteWins + " / " + draws + " / " + blackWins);
          throw new IllegalStateException("Indeterminism detected after " + (j+1) + " games for seed=" + seed);
        }
        endgame = gameplay.getEndGame();
      }
      switch (endgame) {
        case WHITE_WINS -> whiteWins += 1;
        case BLACK_WINS -> blackWins += 1;
        case DRAW -> draws += 1;
        default -> {}
      }
    }
    System.out.println(whiteWins + " / " + draws + " / " + blackWins);
    System.out.println(numberOfDifferentGames * numberOfRepetitions + " games played in " + (System.currentTimeMillis() - startTime) + " ms");
  }
}
