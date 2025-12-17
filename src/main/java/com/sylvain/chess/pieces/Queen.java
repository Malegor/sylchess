package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.HashSet;
import java.util.Set;

public class Queen extends PieceOnBoard {
    public Queen(Color color, Square square) {
        super(color, square);
    }

    @Override
    public Set<Square> getControlledSquares(ChessBoard board) {
        final Set<Square> controlled = new HashSet<>(14);
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

    @Override
    public PieceOnBoard at(Square square) {
        return new Queen(this.color, square);
    }
}
