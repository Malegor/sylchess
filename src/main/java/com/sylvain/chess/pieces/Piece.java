package com.sylvain.chess.pieces;

import com.sylvain.chess.Constants;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
            return Stream.of(ROOK.getControlledSquares(square), BISHOP.getControlledSquares(square)).flatMap(Set::stream).collect(Collectors.toSet());
        }
    }, ROOK {
        @Override
        public boolean isValidAt(final Square square) {
            return ChessBoard.isInBoard(square);
        }

        @Override
        public Set<Square> getControlledSquares(final Square square) {
            final Set<Square> controlled = IntStream.range(1, Constants.BOARD_COLS + 1)
                    .filter(i -> i != square.getColumn())
                    .mapToObj(i -> new Square(i, square.getRow()))
                    .collect(Collectors.toSet());
            controlled.addAll(IntStream.range(1, Constants.BOARD_ROWS + 1)
                    .filter(j -> j != square.getRow())
                    .mapToObj(j -> new Square(square.getColumn(), j))
                    .collect(Collectors.toSet()));
            return controlled;
        }
    }, BISHOP {
        @Override
        public boolean isValidAt(final Square square) {
            return ChessBoard.isInBoard(square);
        }

        @Override
        public Set<Square> getControlledSquares(final Square square) {
            return IntStream.range(1, Constants.BOARD_COLS + 1)
                    .filter(i -> i != square.getColumn())
                    .mapToObj(i -> getControlledSquaresAtColumn(square, i))
                    .flatMap(Collection::stream)
                    .filter(sq -> ChessBoard.isRowInBoard(sq.getRow()))
                    .collect(Collectors.toSet());
        }

        private List<Square> getControlledSquaresAtColumn(final Square originalSquare, final int column) {
            return List.of(
                    new Square(column, column - originalSquare.getColumn() + originalSquare.getRow()),
                    new Square(column, originalSquare.getColumn() - column + originalSquare.getRow())
            );
        }
    }, KNIGHT {
        @Override
        public boolean isValidAt(final Square square) {
            return ChessBoard.isInBoard(square);
        }

        @Override
        public Set<Square> getControlledSquares(final Square square) {
            final List<Integer> possibleValues = List.of(-2, -1, 1, 2);
            return possibleValues.stream()
                    .flatMap(i -> possibleValues.stream()
                            .filter(j -> Math.abs(i) != Math.abs(j))
                            .map(j -> square.move(i, j))
                            .filter(ChessBoard::isInBoard))
                    .collect(Collectors.toSet());
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
