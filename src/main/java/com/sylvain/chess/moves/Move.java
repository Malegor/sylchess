package com.sylvain.chess.moves;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;

import java.util.Map;

public class Move {
    private final Map<PieceOnBoard, PieceOnBoard> moveToNewSquare;
    private final ChessBoard board;
    private PieceOnBoard captured;

    public Move(Map<PieceOnBoard, PieceOnBoard> moveToNewSquare, ChessBoard board) {
        this.moveToNewSquare = moveToNewSquare;
        this.board = board;
        this.captured = null;
    }
    // OBS: map because of special moves (en passant, castling...)
    // OBS: only in case of promo, the first POB is a pawn and the second one is another piece.

    public boolean isValidMove() {
        if (this.moveToNewSquare.isEmpty()) {
            return false;
        }
        final Map.Entry<PieceOnBoard, PieceOnBoard> piece = this.moveToNewSquare.entrySet().iterator().next();
        final Color color = piece.getKey().getColor();
        if (color != piece.getValue().getColor())
            return false;
        if (this.moveToNewSquare.size() > 1) {
            // Castling rules
            // En passant (?)
            // TODO
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
                while (ChessBoard.getPawnDirection(color) * (piece.getValue().getSquare().getRow() - square.getRow()) > 0) {
                    square = square.move(0, ChessBoard.getPawnDirection(color));
                    if (this.board.hasPieceAt(square))
                        return false;
                }
            }
            else {
                // Capture
                final PieceOnBoard pieceAtDestination = this.board.getPieceAt(piece.getValue().getSquare());
                if (pieceAtDestination == null || pieceAtDestination.getColor() == color)
                    return false;
            }
            if (piece.getValue().getSquare().getRow() - piece.getKey().getSquare().getRow() == 2 * ChessBoard.getPawnDirection(color)
                        && piece.getKey().isHasAlreadyMoved())
                return false;
            // 2- The pawn is at its one-before-last row and the next position (getValue) is a piece that is not a pawn or a king.
            if (piece.getValue().getSquare().getRow() == ChessBoard.getPromotionRow(color)) {
                return piece.getValue().isPossiblePromotion();
            }
        }
        this.simulate();
        if (!this.board.piecesCheckingKing(color).isEmpty()) {
            this.rollback();
            return false;
        }
        this.rollback();
        return true;
    }

    private void simulate() {
        for (Map.Entry<PieceOnBoard, PieceOnBoard> move : this.moveToNewSquare.entrySet()) {
            this.captured = this.board.getPieceAt(move.getValue().getSquare());
            this.board.movePiece(move.getKey(), move.getValue());
        }
    }

    public void apply() {
        this.simulate();
        for (Map.Entry<PieceOnBoard, PieceOnBoard> move : this.moveToNewSquare.entrySet()) {
            move.getValue().setHasAlreadyMoved(true);
        }
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
        return "Move{" + moveToNewSquare + '}';
    }
}
