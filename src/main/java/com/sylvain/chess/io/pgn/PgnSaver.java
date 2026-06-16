package com.sylvain.chess.io.pgn;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.GameVariant;
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
  public static String saveGame(final Gameplay game, final ChessBoard board) {
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
        movesBld.append(moveNumber).append(". ").append(Move.NO_WHITE_MOVE_STR).append(move);
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
    return getGameDescription(board, game.getHistory(), game.getEndGame()) + "\n" + movesBld + sep + game.getEndGame().getPgn();
  }

  private static String getGameDescription(final ChessBoard board, final GameHistory history, final EndGame endGame) {
    final StringBuilder builder = new StringBuilder();
    builder.append(tag("Date", LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"))))
            .append(tag("White", history.getFirstPlayerOfColor(PlayerColor.WHITE).toString()))
            .append(tag("Black", history.getFirstPlayerOfColor(PlayerColor.BLACK).toString()));
    if (board.isSetUp())
      builder.append(tag("SetUp", "1"));
    if (!board.getVariant().equals(GameVariant.CLASSICAL))
      builder.append(tag("Variant", board.getVariant().getValue()));
    if (board.getVariant().equals(GameVariant.CHESS960))
      builder.append(tag("Index960", String.valueOf(board.getIndex960())));
    if (board.isSetUp() || !board.getVariant().equals(GameVariant.CLASSICAL))
      builder.append(tag("FEN", history.getInitialFen()));
    if (!endGame.equals(EndGame.STILL_PLAYING)) {
      builder.append(tag("Result", endGame.getPgn()));
      builder.append(tag("Termination", getTermination(history, endGame)));
    }
    return builder.toString();
  }

  private static String tag(final String tagName, final String tagValue) {
    return "[" + tagName + " \"" + tagValue + "\"]\n";
  }

  private static String getTermination(final GameHistory history, final EndGame endGame) {
    final char[] charArray = history.getMovesSan().getLast().toCharArray();
    return charArray[charArray.length - 1] == Move.CHECKMATE_SAN.toCharArray()[0] ? "checkmate" : endGame.equals(EndGame.DRAW) ? "draw" : "abandoned";
  }
}
