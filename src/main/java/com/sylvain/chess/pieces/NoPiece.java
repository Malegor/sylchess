package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.List;

public class NoPiece extends PieceOnBoard {
    public NoPiece(final Color color, final Square square) {
        super(color, square, false);
    }

    @Override
    public NoPiece at(final Square square) {
        return new NoPiece(this.color, square);
    }

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        return List.of();
    }

    @Override
    public char printOnBoard() {
        return ' ';
    }

    @Override
    public boolean isPossiblePromotion() {
        return false;
    }
}
