package com.sylvain.chess.pieces;

import com.sylvain.chess.Constants;
import com.sylvain.chess.board.Square;

import java.util.List;

public enum Piece {
    KING {
        @Override
        public boolean isValidAt(final Square square) {
            return true;
        }
    }, QUEEN {
        @Override
        public boolean isValidAt(final Square square) {
            return true;
        }
    }, ROOK {
        @Override
        public boolean isValidAt(final Square square) {
            return true;
        }
    }, BISHOP {
        @Override
        public boolean isValidAt(final Square square) {
            return true;
        }
    }, KNIGHT {
        @Override
        public boolean isValidAt(final Square square) {
            return true;
        }
    }, PAWN {
        @Override
        public boolean isValidAt(final Square square) {
            return square.getRow() > 1 && square.getRow() < Constants.BOARD_ROWS;
        }
    };

    public abstract boolean isValidAt(final Square square);

    /**
     * @return The (regular) move span independently of the position of other pieces.
     */
    //public abstract List<Square> getMoveSpan();
}
