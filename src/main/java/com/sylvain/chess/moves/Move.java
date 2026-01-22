package com.sylvain.chess.moves;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.NoPiece;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.pieces.Rook;

import java.util.Comparator;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Move {
  public static final String CAPTURE_PGN = "x";
  public static final String CHECK_PGN = "+";
  public static final String CHECKMATE_PGN = "#";
  public static final String KING_SIDE_CASTLE_PGN = "O-O";
  public static final String QUEEN_SIDE_CASTLE_PGN = "O-O-O";
  public static final String PROMO_PGN = "=";

  private final Map<PieceOnBoard, PieceOnBoard> moveToNewSquare;
  private final ChessBoard board;
  private PieceOnBoard captured;

  public Move(Map<PieceOnBoard, PieceOnBoard> moveToNewSquare, ChessBoard board) {
    this.moveToNewSquare = moveToNewSquare;
    this.board = board;
    this.captured = null;
  }

  public boolean isValidMove() {
    if (this.moveToNewSquare.isEmpty())
        return false;
    final Map.Entry<PieceOnBoard, PieceOnBoard> firstEntry = this.moveToNewSquare.entrySet().iterator().next();
    this.captured = this.board.getPieceAt(firstEntry.getValue().getSquare());
    final Color color = firstEntry.getKey().getColor();
    if (color != firstEntry.getValue().getColor())
      throw new IllegalStateException("Illegal move: " + this);
    if ((this.captured != null && this.captured.getColor() == color) ||
            (this.isCastle() && !isValidCastle()) || (firstEntry.getKey().getName().equals(Pawn.NAME_LC) && !isValidPawnMove(firstEntry)))
      return false;
    this.simulate();
    if (!this.board.findPiecesCheckingKing(color).isEmpty()) {
        this.rollback();
        return false;
    }
    this.rollback();
    return true;
  }

  private boolean isValidCastle() {
    // Castling rules: validate that no piece is on the way to the final destination for both the king and the rook, and that no square is controlled
    // along the king's trip. Also check if both pieces are still on the first row.
    if (this.captured != null || this.moveToNewSquare.keySet().stream().anyMatch(p -> p.getSquare().row() != ChessBoard.getFirstRow(p.getColor())))
      return false;
    final Color color = this.getColor();
    int minCol = ChessBoard.BOARD_COLS + 1;
    int maxCol = - 1;
    int minKing=0, maxKing=0;
    King king = null;
    Rook rook = null;
    for (Map.Entry<PieceOnBoard, PieceOnBoard> entry : this.moveToNewSquare.entrySet()) {
      if (entry.getKey().getColor() != color || entry.getValue().getColor() != color)
        throw new IllegalStateException("Illegal move: " + this);
      int minEntry = Math.min(entry.getKey().getSquare().column(), entry.getValue().getSquare().column());
      int maxEntry = Math.max(entry.getKey().getSquare().column(), entry.getValue().getSquare().column());
      minCol = Math.min(minCol, minEntry);
      maxCol = Math.max(maxCol, maxEntry);
      if (entry.getKey().getName().equals(King.NAME_LC)) {
        minKing = minEntry;
        maxKing = maxEntry;
        king = (King) entry.getKey();
      }
      else rook = (Rook) entry.getKey();
    }
    if (king == null)
      throw new IllegalStateException("No king found for castling!");
    for (int col = minCol; col <= maxCol; col++) {
      PieceOnBoard pieceInBetween = board.getPieceAt(new Square(col, king.getSquare().row()));
      if (pieceInBetween != null && !pieceInBetween.equals(king) && !pieceInBetween.equals(rook))
        return false;
    }
    for (int col = minKing; col <= maxKing; col++) {
      // OBS: there would be no need to check the new king position, as it will be done at the end of this method.
      if (!board.piecesControllingSquare(new Square(col, king.getSquare().row()), ChessBoard.getOppositeColor(color)).isEmpty())
        return false;
    }
    return true;
  }

  private boolean isValidPawnMove(final Map.Entry<PieceOnBoard, PieceOnBoard> entry) {
    final Color color = this.getColor();
    // 1- a pawn can move straight or capture in diagonal (special case for the starting position)
    final int rowIncrement = ChessBoard.getPawnDirection(color) * (entry.getValue().getSquare().row() - entry.getKey().getSquare().row());
    if (Math.abs(entry.getValue().getSquare().column() - entry.getKey().getSquare().column()) > 1
                  || rowIncrement > 2
                  || rowIncrement < 1)
      return false;
    if (entry.getValue().getSquare().column() == entry.getKey().getSquare().column()) {
      // This is not a capture, no entry can be on the way.
      Square square = entry.getKey().getSquare();
      if (ChessBoard.getPawnDirection(color) * (entry.getValue().getSquare().row() - square.row()) == 2
              && ChessBoard.getRowForColor(square.row(), color) > 2) {
        return false;
      }
      while (ChessBoard.getPawnDirection(color) * (entry.getValue().getSquare().row() - square.row()) > 0) {
          square = square.move(0, ChessBoard.getPawnDirection(color));
          if (this.board.hasPieceAt(square))
            return false;
      }
    }
    else {
      // Capture or en-passant
      if (ChessBoard.getPawnDirection(color) * (entry.getValue().getSquare().row() - entry.getKey().getSquare().row()) == 2)
        return false;
      if (this.captured == null) {
        // Validate en-passant
        final PieceOnBoard potentialPieceEnPassant = this.board.getPieceAt(entry.getValue().getSquare().move(0, - ChessBoard.getPawnDirection(color)));
        this.captured = potentialPieceEnPassant;
        if (potentialPieceEnPassant == null || !potentialPieceEnPassant.getName().equals(Pawn.NAME_LC) ||
                (this.board.getPreviousMove() != null ?
                !(this.board.getPreviousMove().getDestinationPiece().getName().equals(Pawn.NAME_LC))
                || (ChessBoard.getPawnDirection(color) * this.board.getPreviousMove().getDestinationPiece().getSquare().row()
                        - this.board.getPreviousMove().moveToNewSquare.values().iterator().next().getSquare().row()) >= 2 :
                        potentialPieceEnPassant.getSquare().row() != ChessBoard.getRowForColor(3, color)))
          return false;
      }
      if (this.captured == null || this.captured.getColor() == color) {
        return false;
      }
    }
    if (entry.getValue().getSquare().row() - entry.getKey().getSquare().row() == 2 * ChessBoard.getPawnDirection(color)
                && ChessBoard.getPawnDirection(color) * entry.getKey().getSquare().row() >= 3)
      return false;
    // 2- The pawn is at its one-before-last row and the next position (getValue) is an entry that is not a pawn or a king.
    return entry.getValue().getSquare().row() != ChessBoard.getPromotionRow(color) || entry.getValue().isPossiblePromotion();
  }

  public void simulate() {
      for (Map.Entry<PieceOnBoard, PieceOnBoard> move : this.moveToNewSquare.entrySet()) {
        if (this.captured != null) {
          this.board.removePiece(this.captured);
        }
        this.board.simulatePieceMove(move.getKey(), move.getValue());
      }
  }

  public void apply() {
  // TODO: this method should be orchestrated by CB?
      this.simulate();
      for (PieceOnBoard piece : this.moveToNewSquare.values()) {
          piece.setHasAlreadyMoved(true);
      }
      this.board.setPreviousMove(this);
  }

  public void rollback() {
      for (Map.Entry<PieceOnBoard, PieceOnBoard> entry : this.moveToNewSquare.entrySet()) {
          this.board.removePiece(entry.getValue());
          this.board.addPiece(entry.getKey());
      }
      if (this.captured != null) this.board.addPiece(this.captured);
  }

  @Override
  public String toString() {
      return "Move" + moveToNewSquare;
  }

  /**
   * @return The piece at its destination after the move. In case of castling, returns the destination of the rook.
   */
  public PieceOnBoard getDestinationPiece() {
    return this.isCastle() ? this.moveToNewSquare.values().stream().filter(p -> p.getName().equals(Rook.NAME_LC)).findFirst().orElse(null)
            : this.moveToNewSquare.values().iterator().next();
  }

  public boolean involvesPawnOrCapture() {
    return this.captured != null || this.moveToNewSquare.keySet().iterator().next().getName().equals(Pawn.NAME_LC);
  }

  public boolean isPawnTwoSquareMove() {
    final Map.Entry<PieceOnBoard, PieceOnBoard> moveEntry = this.moveToNewSquare.entrySet().iterator().next();
    return this.moveToNewSquare.size() == 1 && moveEntry.getValue().getName().equals(Pawn.NAME_LC) &&
            ChessBoard.getPawnDirection(moveEntry.getKey().getColor()) * (moveEntry.getValue().getSquare().row() - moveEntry.getKey().getSquare().row()) == 2;
  }

  public boolean isCastle() {
    return this.moveToNewSquare.size() > 1;
  }

  public Color getColor() {
    return this.moveToNewSquare.keySet().iterator().next().getColor();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null || getClass() != o.getClass()) return false;
    final Move move = (Move) o;
    return Objects.equals(this.toString(), move.toString());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(this.toString());
  }

  public String toPgn() {
    if (this.isCastle()) {
      final boolean isKingSide = this.moveToNewSquare.keySet().stream().min(Comparator.comparing(PieceOnBoard::getSquare)).orElse(new NoPiece(this.getColor(), new Square(0,0))).getName().equals(King.NAME_LC);
      return isKingSide ? KING_SIDE_CASTLE_PGN : QUEEN_SIDE_CASTLE_PGN;
    }
    final Map.Entry<PieceOnBoard, PieceOnBoard> moveEntry = this.moveToNewSquare.entrySet().iterator().next();
    final String takeStr = this.captured == null ? "" : CAPTURE_PGN;
    final PieceOnBoard originalPiece = moveEntry.getKey();
    final Square startSquare = originalPiece.getSquare();
    final String pieceStr = !originalPiece.getName().equals(Pawn.NAME_LC) ? String.valueOf(Character.toUpperCase(originalPiece.printOnBoard()))
            : this.captured == null ? "" : String.valueOf(startSquare.getColumnLetter());
    final Square destSquare = moveEntry.getValue().getSquare();
    final Set<PieceOnBoard> samePiecesForDestination = board.piecesControllingSquare(destSquare, this.getColor()).stream().filter(p -> !p.equals(originalPiece)
            && p.getClass().equals(originalPiece.getClass())).collect(Collectors.toSet());
    final boolean shouldDisambiguateRow = samePiecesForDestination.stream().anyMatch(p -> p.getSquare().column() == startSquare.column());
    final boolean shouldDisambiguateBoth = shouldDisambiguateRow && samePiecesForDestination.stream().anyMatch(p -> p.getSquare().row() == startSquare.row());
    final String disambiguate = String.valueOf(originalPiece.getName().equals(Pawn.NAME_LC) || samePiecesForDestination.isEmpty() ?
            "" : shouldDisambiguateBoth ?
            startSquare.toString() : shouldDisambiguateRow ?
            startSquare.row() : String.valueOf(startSquare.getColumnLetter()));
    final String promoStr = originalPiece.getClass().equals(moveEntry.getValue().getClass()) ? "" : PROMO_PGN + Character.toUpperCase(moveEntry.getValue().printOnBoard());
    final String status = ""; // TODO: checkmate, check ... ?? or should it be the responsibility of the gameplay?
    return pieceStr + disambiguate + takeStr + destSquare + promoStr + status;
  }
}
