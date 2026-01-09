package com.sylvain.chess.io.fen;

import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.play.Gameplay;

public class FenSaver {
  public static String getPositionString(final Gameplay game) {
    final String boardString = getBoardString(game.getBoard());
    final Character colorString = game.getLastPlayer().getColor().getFenName();
    return null;
  }

  public static String getBoardString(final ChessBoard board) {
    final StringBuilder boardString = new StringBuilder();
    String sep = "";
    for (int row = 8; row > 0; row--) {
      int numberOfEmptySquares = 0;
      boardString.append(sep);
      for (int col = 1; col <= 8; col++) {
        final PieceOnBoard piece = board.getPieceAt(new Square(col, row));
        if (piece != null) {
          if (numberOfEmptySquares != 0) {
            boardString.append(numberOfEmptySquares);
            numberOfEmptySquares = 0;
          }
          boardString.append(piece.printOnBoard());
        }
        else
          numberOfEmptySquares++;
      }
      if (numberOfEmptySquares != 0)
        boardString.append(numberOfEmptySquares);
      sep = FenLoader.ROW_SEP;
    }
    return boardString.toString();
  }
}
