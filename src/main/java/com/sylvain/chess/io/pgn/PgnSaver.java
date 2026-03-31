package com.sylvain.chess.io.pgn;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.play.GameHistory;
import com.sylvain.chess.play.Gameplay;

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
    movesBld.append(sep).append(game.getEndGame().getPgn());
    return movesBld.toString();
  }
}
