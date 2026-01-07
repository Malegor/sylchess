package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.ArrayList;
import java.util.List;

public class Queen extends PieceOnBoard {
    public Queen(final Color color, final Square square) {
        this(color, square, false);
    }

    public Queen(final Color color, final Square square, final boolean hasAlreadyMoved) {
        super(color, square, hasAlreadyMoved);
    }

    @Override
    public Queen at(final Square square) {
        return new Queen(this.color, square, this.hasAlreadyMoved);
    }

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        final List<Square> controlled = new ArrayList<>(14);
        controlled.addAll(new Bishop(this.color, this.square).getControlledSquares(board));
        controlled.addAll(new Rook(this.color, this.square).getControlledSquares(board));
        return controlled;
    }

    @Override
    public char printOnBoard() {
        return this.color == Color.WHITE ? 'Q' : 'q';
    }

    @Override
    public boolean isPossiblePromotion() {
        return true;
    }
}
