package com.sylvain.chess.io;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.players.DummyPlayer;

import java.util.List;

public class FenDecoder {

  public static Gameplay loadPosition(final String fen) {
    final String[] fenArray = fen.split(" ");
    if (fenArray.length < 6) {
      throw new IllegalArgumentException("Invalid fen (missing arguments): " + fen);
    }
    final ChessBoard board = loadBoard(fenArray[0]);
    // TODO: read other fields
    return new Gameplay(board, List.of(new DummyPlayer(Color.WHITE), new DummyPlayer(Color.BLACK))); // TODO: players??
  }

  public static ChessBoard loadBoard(final String fenBoard) {
    final ChessBoard board = new ChessBoard();
    final String[] fenByRow = fenBoard.split("/");
    if (fenByRow.length != ChessBoard.BOARD_ROWS) {
      throw new IllegalArgumentException("Invalid fen board (invalid rows): " + fenBoard);
    }
    for (int row = 0; row < fenByRow.length; row++) {
      final String currentRow = fenByRow[row];
      int col = 0;
      for (char character : currentRow.toCharArray()) {
        col++;
        if (Character.isDigit(character)) {
          final int digit = Character.getNumericValue(character);
          if (digit == 0)
            throw new IllegalStateException("Invalid fen board character (invalid digits): " + currentRow);
          col += digit - 1;
        }
        else if (Character.isLetter(character)) {
          board.addPiece(PieceOnBoard.createPiece(character, new Square(col, 8 - row)));
        }
        else
          throw new IllegalStateException("Invalid fen board character: " + currentRow);
      }
    }
    return board;
  }
}
