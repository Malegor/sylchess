package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.InteractivePlayer;
import com.sylvain.chess.play.players.MateSolver;
import com.sylvain.chess.play.players.Player;
import lombok.extern.log4j.Log4j2;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

@Log4j2
public class InteractiveGame {
  public static void main(String[] args) {
    final InputStream inputStream = System.in; // TODO: param for tests(?)
    final Scanner scanner = new Scanner(inputStream);
    final String solverName = "solver";
    final List<Player> players = new ArrayList<>(2);
    System.out.print("Enter fen description for board to load (leave empty for classical board: ");
    final String fen = scanner.nextLine();
    final Gameplay game = getGame(fen);
    for (final Color color : Color.values()) {
      System.out.print("Enter name for " + color + " (leave empty to play a dummy player, or \"" + solverName + "\" for a puzzle solver): ");
      final String playerName = scanner.nextLine();
      players.add(getPlayer(playerName, color, game.getBoard(), scanner));
    }
    play(game, players);
    scanner.close();
  }

  private static Gameplay getGame(final String fen) {
    return fen.isEmpty() ? new Gameplay(ChessBoard.defaultBoard()) : FenLoader.loadPosition(fen);
  }

  private static Player getPlayer(final String playerName, final Color color, final ChessBoard board, final Scanner scanner) {
    final String solverName = "solver";
    return playerName.equals(solverName) ? new MateSolver(color, board, 5)
            : playerName.isEmpty() ? new DummyPlayer(color, board)
            : new InteractivePlayer(color, playerName, board, scanner);
  }

  public static EndGame play(final Gameplay game, final List<Player> players) {
    game.playGame(players);
    log.info(game.getEndGame().getPgn());
    return game.getEndGame();
  }
}
