package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.List;
import java.util.stream.Collectors;

public class Knight extends PieceOnBoard {
    public Knight(final Color color, final Square square) {
        this(color, square, false);
    }

    public Knight(final Color color, final Square square, final boolean hasAlreadyMoved) {
        super(color, square, hasAlreadyMoved);
    }

    @Override
    public Knight at(final Square square) {
        return new Knight(this.color, square, this.hasAlreadyMoved);
    }

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        final List<Integer> knightJump = List.of(-2, -1, 1, 2);
        return knightJump.stream()
                .flatMap(i -> knightJump.stream()
                        .filter(j -> Math.abs(i) != Math.abs(j))
                        .map(j -> square.move(i, j))
                        .filter(ChessBoard::isInBoard))
                .collect(Collectors.toList());
    }

    @Override
    public char printOnBoard() {
        return this.color == Color.WHITE ? 'N' : 'n';
    }

    @Override
    public boolean isPossiblePromotion() {
        return true;
    }
}
