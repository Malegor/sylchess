package com.sylvain.chess.pieces;

import com.sylvain.chess.Constants;

public enum Piece {
    KING {
        @Override
        public boolean isValidAt(int column, int row) {
            return true;
        }
    }, QUEEN {
        @Override
        public boolean isValidAt(int column, int row) {
            return true;
        }
    }, ROOK {
        @Override
        public boolean isValidAt(int column, int row) {
            return true;
        }
    }, BISHOP {
        @Override
        public boolean isValidAt(int column, int row) {
            return true;
        }
    }, KNIGHT {
        @Override
        public boolean isValidAt(int column, int row) {
            return true;
        }
    }, PAWN {
        @Override
        public boolean isValidAt(int column, int row) {
            return row > 1 && row < Constants.BOARD_ROWS;
        }
    };

    public abstract boolean isValidAt(final int column, final int row);
}
