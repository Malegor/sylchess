package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.ArrayList;
import java.util.List;

public class Bishop extends PieceOnBoard {
    public Bishop(Color color, Square square) {
        super(color, square);
    }

    @Override
    public List<Square> getControlledSquares(ChessBoard board) {
        final List<Square> controlled = new ArrayList<>(14);
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, -1, -1));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, -1, 1));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, 1, -1));
        controlled.addAll(this.getControlledSquaresInSingleDirection(board, 1, 1));
        return controlled;
    }

    @Override
    public char printOnBoard() {
        return this.color == Color.WHITE ? 'B' : 'b';
    }

    @Override
    public boolean isPossiblePromotion() {
        return true;
    }

    @Override
    public Bishop at(Square square) {
        return new Bishop(this.color, square);
    }
}
