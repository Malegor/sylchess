package com.sylvain.chess.moves;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.pieces.Rook;
import lombok.Getter;

import java.util.Map;

public class Move {
  @Getter
  private final Map<PieceOnBoard, PieceOnBoard> moveToNewSquare;
  private final ChessBoard board;
  private PieceOnBoard captured;

  public Move(Map<PieceOnBoard, PieceOnBoard> moveToNewSquare, ChessBoard board) {
    this.moveToNewSquare = moveToNewSquare;
    this.board = board;
    this.captured = null;
  }

    public boolean isValidMove() {
      if (this.moveToNewSquare.isEmpty()) {
          return false;
      }
      final Map.Entry<PieceOnBoard, PieceOnBoard> piece = this.moveToNewSquare.entrySet().iterator().next();
      this.captured = this.board.getPieceAt(piece.getValue().getSquare());
        final Color color = piece.getKey().getColor();
      if (this.captured != null && this.captured.getColor() == color)
        return false;
      if (color != piece.getValue().getColor())
          return false;
      if (this.moveToNewSquare.size() > 1) {
          // Castling rules: validate that no piece is on the way to the final destination for both the king and the rook, and that no square is controlled.
        int minCol = ChessBoard.BOARD_COLS + 1;
        int maxCol = - 1;
        int minKing=0, maxKing=0;
        King king = null;
        Rook rook = null;
        for (Map.Entry<PieceOnBoard, PieceOnBoard> entry : this.moveToNewSquare.entrySet()) {
          int minEntry = Math.min(entry.getKey().getSquare().getColumn(), entry.getValue().getSquare().getColumn());
          int maxEntry = Math.max(entry.getKey().getSquare().getColumn(), entry.getValue().getSquare().getColumn());
          minCol = Math.min(minCol, minEntry);
          maxCol = Math.max(maxCol, maxEntry);
          if (entry.getKey() instanceof King) {
            minKing = minEntry;
            maxKing = maxEntry;
            king = (King) entry.getKey();
          }
          else rook = (Rook) entry.getKey();
        }
        for (int col = minCol; col <= maxCol; col++) {
          PieceOnBoard pieceInBetween = board.getPieceAt(new Square(col, king.getSquare().getRow()));
          if (pieceInBetween != null && !pieceInBetween.equals(king) && !pieceInBetween.equals(rook))
            return false;
        }
        for (int col = minKing; col <= maxKing; col++) {
          // OBS there would be no need to check the new king position, as it will be done at the end of this method.
          if (!board.piecesControllingSquare(new Square(col, king.getSquare().getRow()), ChessBoard.getOppositeColor(color)).isEmpty())
            return false;
        }
      }
      // TODO: object orient this piece of code (remove instanceof)
      else if (this.moveToNewSquare.entrySet().iterator().next().getKey() instanceof Pawn) {
          // 1- a pawn can move straight or capture in diagonal (special case for the starting position)
        int rowIncrement = ChessBoard.getPawnDirection(color) * (piece.getValue().getSquare().getRow() - piece.getKey().getSquare().getRow());
        if (Math.abs(piece.getValue().getSquare().getColumn() - piece.getKey().getSquare().getColumn()) > 1
                      || rowIncrement > 2
                      || rowIncrement < 1)
              return false;
        if (piece.getValue().getSquare().getColumn() == piece.getKey().getSquare().getColumn()) {
          // This is not a capture, no piece can be on the way.
          Square square = piece.getKey().getSquare();
          if (ChessBoard.getPawnDirection(color) * (piece.getValue().getSquare().getRow() - square.getRow()) == 2
                  && ChessBoard.getRowForColor(square.getRow(), color) > 2) {
            return false;
          }
          while (ChessBoard.getPawnDirection(color) * (piece.getValue().getSquare().getRow() - square.getRow()) > 0) {
              square = square.move(0, ChessBoard.getPawnDirection(color));
              if (this.board.hasPieceAt(square))
                  return false;
          }
        }
        else {
          // Capture or en-passant
          if (ChessBoard.getPawnDirection(color) * (piece.getValue().getSquare().getRow() - piece.getKey().getSquare().getRow()) == 2)
            return false;
          if (this.captured == null) {
            // Validate en-passant
            final PieceOnBoard potentialPieceEnPassant = this.board.getPieceAt(piece.getValue().getSquare().move(0, - ChessBoard.getPawnDirection(color)));
            this.captured = potentialPieceEnPassant;
            if (!(potentialPieceEnPassant instanceof Pawn) ||
                    (this.board.getPreviousMove() != null ?
                    !(this.board.getPreviousMove().getDestinationPiece() instanceof Pawn)
                    || (ChessBoard.getPawnDirection(color) * this.board.getPreviousMove().getDestinationPiece().getSquare().getRow()
                            - this.board.getPreviousMove().moveToNewSquare.values().iterator().next().getSquare().getRow()) >= 2 :
                            potentialPieceEnPassant.getSquare().getRow() != ChessBoard.getRowForColor(3, color)))
              return false;
          }
          if (this.captured == null || this.captured.getColor() == color) {
            return false;
          }
        }
        if (piece.getValue().getSquare().getRow() - piece.getKey().getSquare().getRow() == 2 * ChessBoard.getPawnDirection(color)
                    && ChessBoard.getPawnDirection(color) * piece.getKey().getSquare().getRow() >= 3)
            return false;
        // 2- The pawn is at its one-before-last row and the next position (getValue) is a piece that is not a pawn or a king.
        if (piece.getValue().getSquare().getRow() == ChessBoard.getPromotionRow(color) && !piece.getValue().isPossiblePromotion()) {
            return false;
        }
      }
      this.simulateForCheckValidate();
      if (!this.board.piecesCheckingKing(color).isEmpty()) {
          this.rollback();
          return false;
      }
      this.rollback();
      return true;
    }

    private void simulateForCheckValidate() {
        for (Map.Entry<PieceOnBoard, PieceOnBoard> move : this.moveToNewSquare.entrySet()) {
          if (this.captured != null) {
            this.board.removePiece(this.captured);
          }
          this.board.simulatePieceMove(move.getKey(), move.getValue());
        }
    }

    public void apply() {
    // TODO: this method should be orchestrated by CB?
        this.simulateForCheckValidate();
        for (PieceOnBoard piece : this.moveToNewSquare.values()) {
            piece.setHasAlreadyMoved(true);
        }
        this.board.setPreviousMove(this);
    }

    private void rollback() {
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

    public PieceOnBoard getDestinationPiece() {
      return this.moveToNewSquare.size() == 1 ? this.moveToNewSquare.values().iterator().next() : null;
    }
    
    public boolean involvesPawnOrCapture() {
      return this.captured != null || this.moveToNewSquare.keySet().iterator().next() instanceof Pawn;
    }
}
