package com.sylvain.chess.io.fen;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.GameVariant;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import com.sylvain.chess.play.GameStateInfo;
import com.sylvain.chess.play.Gameplay;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class FenSaver {
  public static String getPositionString(final Gameplay game) {
    final String boardString = getBoardString(game.getBoard());
    final GameStateInfo info = game.getInfo();
    final Character colorString = ChessBoard.getOppositeColor(info.getLastPlayer().getColor()).getFenName();
    final String allPossibleCastles = getPossibleCastles(game.getBoard());
    final String possibleEnPassantSquare = getPossibleEnPassant(game.getBoard());
    final int halfMoveNumber = info.getHalfMoveNumber();
    final int numberOfHalfMovesWithoutImprovement = halfMoveNumber - info.getLastHalfMoveWithCaptureOrPawn();
    return boardString + FenLoader.SEP + colorString + FenLoader.SEP + allPossibleCastles + FenLoader.SEP + possibleEnPassantSquare + FenLoader.SEP +
            numberOfHalfMovesWithoutImprovement + FenLoader.SEP + info.getMoveNumber();
  }

  private static String getPossibleEnPassant(final ChessBoard board) {
    return board.getPreviousMove() == null || !board.getPreviousMove().isPawnTwoSquareMove() ? FenLoader.NONE :
            board.getPreviousMove().getDestinationPiece().getSquare().move(0, - ChessBoard.getPawnDirection(board.getPreviousMove().getColor())).toString();
  }

  private static String getPossibleCastles(final ChessBoard board) {
    final StringBuilder builder = new StringBuilder();
    for (PlayerColor color : board.getColors()) { // OBS: better to get colors from the game players?
      final King king = board.getKing(color);
      if (king != null && !king.isHasAlreadyMoved()) {
        final List<Character> colorChars = new ArrayList<>(2);
        final StringBuilder builderColor = new StringBuilder();
        final List<Rook> rooks = board.getUnmovedRooks(color);
        final boolean isClassicalGame = board.getVariant().equals(GameVariant.CLASSICAL) && king.getSquare().column() == ChessBoard.CLASSICAL_KING_COLUMN
                && rooks.stream().noneMatch(rook -> !rook.isHasAlreadyMoved()
                  && !Set.of(Square.getColumnLetter(1), Square.getColumnLetter(ChessBoard.BOARD_COLS)).contains(rook.getSquare().getColumnLetter()));
        for (boolean kingSide : List.of(Boolean.TRUE, Boolean.FALSE)) {
          for (Rook rook: rooks) {
            if (!rook.isHasAlreadyMoved() && ChessBoard.areValidSquaresForCastle(king, rook, kingSide)) {
              final char columnLetter = rook.getSquare().getColumnLetter();
              colorChars.add(columnLetter);
            }
          }
        }
        // OBS: the order should be "kq" (which is the natural order) but the reverse order for a...h
        // This is why the sorting is done before replacing the letters k and q, as 'k' is 'h' and 'q' is 'a'.
        colorChars
                .stream()
                .sorted(Comparator.reverseOrder())
                .map(c -> isClassicalGame &&
                        (c == Square.getColumnLetter(1) || c == Square.getColumnLetter(ChessBoard.BOARD_COLS)) ?
                        c == Square.getColumnLetter(1) ? Queen.NAME_LC : King.NAME_LC :
                        c)
                .forEach(c -> builderColor.append(color.changeChar().apply(c)));
        builder.append(builderColor);
      }
    }
    return builder.isEmpty() ? FenLoader.NONE : builder.toString();
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
