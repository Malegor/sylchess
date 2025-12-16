package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.HashSet;
import java.util.Set;

public class Rook extends PieceForCastling {
    public Rook(Color color, Square square) {
        super(color, square);
    }

    @Override
    public Set<Square> getControlledSquares(ChessBoard board) {
        final Set<Square> controlled = new HashSet<>(14);
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
}
