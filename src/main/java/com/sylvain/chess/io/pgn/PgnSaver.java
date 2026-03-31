package com.sylvain.chess.io.pgn;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.play.EndGame;
import com.sylvain.chess.play.GameHistory;
import com.sylvain.chess.play.Gameplay;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Based on specification at <url><a href="https://github.com/mliebelt/pgn-spec-commented/blob/main/pgn-specification.txt">mliebelt github</a></url>
 */
public class PgnSaver {
  public static String saveGame(final Gameplay game) {
    final GameHistory history = game.getHistory();
    int moveNumber = history.getFirstMoveNumber();
    boolean newMove = history.getFirstPlayingColor().equals(PlayerColor.WHITE);
    final StringBuilder movesBld = new StringBuilder();
    boolean isFirstMove = true;
    String sep = "";
    for (final String move : history.getMovesSan()) {
      if (newMove) {
        movesBld.append(sep).append(moveNumber).append(". ").append(move);
        newMove = false;
      }
      else if (isFirstMove) {
        movesBld.append(moveNumber).append(". ").append(Move.NO_MOVE_STR).append(move);
        moveNumber++;
        newMove = true;
      }
      else {
        // TODO constants for columns
        movesBld.append(" ").append(move);
        moveNumber++;
        newMove = true;
      }
      isFirstMove = false;
      sep = " ";
    }
    return getGameDescription(game) + "\n\n" + movesBld + sep + game.getEndGame().getPgn();
  }

  private static String getGameDescription(final Gameplay game) {
    return "[Date \"" + LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "\"]" +
            "\n[White \"" + game.getHistory().getFirstPlayerOfColor(PlayerColor.WHITE) + "\"]" +
            "\n[Black \"" + game.getHistory().getFirstPlayerOfColor(PlayerColor.BLACK) + "\"]" +
            (game.getEndGame().equals(EndGame.PLAYING) ? "" :
                    "\n[Result \"" + game.getEndGame().getPgn() + "\"]" +
                    "\n[Termination \"" + getTermination(game) + "\"]");
  }

  private static String getTermination(final Gameplay game) {
    final char[] charArray = game.getHistory().getMovesSan().getLast().toCharArray();
    return charArray[charArray.length - 1] == Move.CHECKMATE_SAN.toCharArray()[0] ? "checkmate" : game.getEndGame().equals(EndGame.DRAW) ? "draw" : "abandoned";
  }
}
