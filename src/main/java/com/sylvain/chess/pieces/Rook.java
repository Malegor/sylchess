package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.ArrayList;
import java.util.List;

public class Rook extends PieceOnBoard {
    public Rook(final Color color, final Square square) {
        this(color, square, false);
    }

    public Rook(final Color color, final Square square, final boolean hasAlreadyMoved) {
        super(color, square, hasAlreadyMoved);
    }

    @Override
    public Rook at(final Square square) {
        return new Rook(this.color, square, this.hasAlreadyMoved);
    }

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        final List<Square> controlled = new ArrayList<>(14);
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, -1, 0));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, 1, 0));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, 0, -1));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, 0, 1));
        return controlled;
    }

    @Override
    public char printOnBoard() {
        return this.color == Color.WHITE ? 'R' : 'r';
    }

    @Override
    public boolean isPossiblePromotion() {
        return true;
    }
}
