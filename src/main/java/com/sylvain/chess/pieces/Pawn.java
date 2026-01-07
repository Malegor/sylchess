package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.ArrayList;
import java.util.List;

public class Pawn extends PieceOnBoard {

    public Pawn(final Color color, final Square startingSquare) {
        this(color, startingSquare, false);
    }

    public Pawn(final Color color, final Square square, final boolean hasAlreadyMoved) {
        super(color, square, hasAlreadyMoved);
    }

    @Override
    public Pawn at(final Square square) {
        return new Pawn(this.color, square, this.hasAlreadyMoved);
    }
    public Rook toRook(final Square square) {
        return new Rook(this.color, square, this.hasAlreadyMoved);
    }
    public Bishop toBishop(final Square square) {
        return new Bishop(this.color, square, this.hasAlreadyMoved);
    }
    public Knight toKnight(final Square square) {
        return new Knight(this.color, square, this.hasAlreadyMoved);
    }
    public Queen toQueen(final Square square) {return new Queen(this.color, square, this.hasAlreadyMoved);}

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        final List<Square> controlled = new ArrayList<>(2);
        final int updateRow = ChessBoard.getPawnDirection(color);
        if (square.column() > 1) controlled.add(square.move(-1, updateRow));
        if (square.column() < ChessBoard.BOARD_COLS) controlled.add(square.move(1, updateRow));
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
}
