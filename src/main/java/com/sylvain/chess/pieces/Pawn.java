package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.HashSet;
import java.util.Set;

public class Pawn extends PieceOnBoard {

    public Pawn(Color color, Square square) {
        super(color, square);
    }

    @Override
    public Set<Square> getControlledSquares(ChessBoard board) {
        final Set<Square> controlled = new HashSet<>();
        final int updateRow = ChessBoard.getPawnDirection(color);
        if (square.getColumn() > 1) controlled.add(square.move(-1, updateRow));
        if (square.getColumn() < ChessBoard.BOARD_COLS) controlled.add(square.move(1, updateRow));
        return controlled;
    }

    @Override
    public char printOnBoard() {
        return this.color == Color.WHITE ? 'P' : 'p';
    }

    @Override
    public boolean isPossiblePromotion() {
        return false;
    }

    @Override
    public PieceOnBoard at(Square square) {
        return new Pawn(this.color, square);
    }
}
