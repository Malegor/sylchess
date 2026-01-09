package com.sylvain.chess.io.fen;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.pieces.Rook;
import com.sylvain.chess.play.Gameplay;

import java.util.List;
import java.util.Set;

public class FenSaver {
  public static String getPositionString(final Gameplay game) {
    final String boardString = getBoardString(game.getBoard());
    final Character colorString = ChessBoard.getOppositeColor(game.getLastPlayer().getColor()).getFenName();
    final String allPossibleCastles = getPossibleCastles(game.getBoard());
    final String possibleEnPassantSquare = getPossibleEnPassant(game.getBoard());
    //final int
    return null;
  }

  private static String getPossibleEnPassant(final ChessBoard board) {
    return board.getPreviousMove() == null || !board.getPreviousMove().isPawnTwoSquareMove() ? "-" :
            board.getPreviousMove().getDestinationPiece().getSquare().move(0, - ChessBoard.getPawnDirection(board.getPreviousMove().getColor())).toString();
  }

  private static String getPossibleCastles(final ChessBoard board) {
    final StringBuilder builder = new StringBuilder();
    for (Color color : board.getColors()) { // OBS: better to get colors from the game players?
      final King king = board.getKing(color);
      if (!king.isHasAlreadyMoved()) {
        final Set<Rook> rooks = board.getUnmovedRooks(color);
        for (boolean kingSide : List.of(Boolean.TRUE, Boolean.FALSE)) {
          for (Rook rook: rooks) {
            if (!rook.isHasAlreadyMoved() && ChessBoard.areValidSquaresForCastle(king, rook, kingSide)) {
              builder.append(color.change().apply(kingSide ? 'k' : 'q'));
              // OBS: in special games starting with over 2 rooks, this could lead to the same castle several times...
              // TODO: FEN in 960 is not the same
            }
          }
        }
      }
    }
    return builder.isEmpty() ? "-" : builder.toString();
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
