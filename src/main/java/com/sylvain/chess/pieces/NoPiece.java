package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.Set;

public class NoPiece extends PieceOnBoard {
    public NoPiece(Color color, Square square) {
        super(color, square);
    }

    @Override
    public Set<Square> getControlledSquares(ChessBoard board) {
        return Set.of();
    }

    @Override
    public char printOnBoard() {
        return ' ';
    }

    @Override
    public boolean isPossiblePromotion() {
        return false;
    }

    @Override
    public PieceOnBoard at(Square square) {
        return new NoPiece(this.color, square);
    }
}
