package com.sylvain.chess.pieces;

import com.sylvain.chess.Constants;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.HashSet;
import java.util.Set;

public enum Piece {
    KING {
        @Override
        public boolean isValidAt(final Square square) {
            return ChessBoard.isInBoard(square);
        }

        @Override
        public Set<Square> getControlledSquares(final Square square) {
            return Set.of();
        }
    }, QUEEN {
        @Override
        public boolean isValidAt(final Square square) {
            return ChessBoard.isInBoard(square);
        }

        @Override
        public Set<Square> getControlledSquares(final Square square) {
            return Set.of();
        }
    }, ROOK {
        @Override
        public boolean isValidAt(final Square square) {
            return ChessBoard.isInBoard(square);
        }

        @Override
        public Set<Square> getControlledSquares(final Square square) {
            return Set.of();
        }
    }, BISHOP {
        @Override
        public boolean isValidAt(final Square square) {
            return ChessBoard.isInBoard(square);
        }

        @Override
        public Set<Square> getControlledSquares(final Square square) {
            return Set.of();
        }
    }, KNIGHT {
        @Override
        public boolean isValidAt(final Square square) {
            return ChessBoard.isInBoard(square);
        }

        public Set<Square> getControlledSquares(final Square square) {
            final Set<Square> controlled = new HashSet<>();
            final int [] possibleValues = new int[]{-2, -1, 1, 2};
            for (int i : possibleValues) {
                for (int j : possibleValues) {
                    // abs(i) and abs(j) must be distinct
                    if (Math.abs(i) != Math.abs(j)) {
                        final Square newSquare = square.move(i, j);
                        if (ChessBoard.isInBoard(newSquare)) {
                            controlled.add(newSquare);
                        }
                    }
                }
            }
            return controlled;
        }
    }, PAWN {
        @Override
        public boolean isValidAt(final Square square) {
            return square.getRow() > 1 && square.getRow() < Constants.BOARD_ROWS;
        }

        @Override
        public Set<Square> getControlledSquares(final Square square) {
            final Set<Square> controlled = new HashSet<>();
            if (square.getColumn() > 1) controlled.add(square.move(-1,1));
            if (square.getColumn() < Constants.BOARD_COLS) controlled.add(square.move(1, 1));
            return controlled;
        }
    };

    public abstract boolean isValidAt(final Square square);

    /**
     * OBS: This is only different for the pawn, as any other piece has its controlled squares the same as the possible moves.
     * @return The (regular) controlled squares independently of the position of other pieces.
     */
    public abstract Set<Square> getControlledSquares(final Square square);
}
