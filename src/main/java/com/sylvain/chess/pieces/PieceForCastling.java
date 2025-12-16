package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.Square;

public abstract class PieceForCastling extends PieceOnBoard {
    private boolean canCastle;

    public PieceForCastling(Color color, Square square) {
        super(color, square);
        this.canCastle = true;
    }

    public void hasAlreadyMoved() {
        this.canCastle = false;
    }
}
