package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends PieceOnBoard {
    public static char NAME_LC = 'b';

    public Bishop(final Color color, final Square square) {
        this(color, square, false);
    }

    public Bishop(final Color color, final Square square, final boolean hasAlreadyMoved) {
        super(color, square, hasAlreadyMoved);
    }

    @Override
    public Bishop at(final Square square) {
        return new Bishop(this.color, square, this.hasAlreadyMoved);
    }

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        final List<Square> controlled = new ArrayList<>(14);
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, -1, -1));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, -1, 1));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, 1, -1));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, 1, 1));
        return controlled;
    }

    @Override
    public Character getName() {
        return NAME_LC;
    }

    @Override
    public boolean isPossiblePromotion() {
        return true;
    }
}
