package com.sylvain.chess.pieces;

import com.sylvain.chess.Constants;

public class Pawn implements Piece {
    @Override
    public boolean isValidAt(int column, int row) {
        return row > 1 && row < Constants.BOARD_ROWS;
    }
}
