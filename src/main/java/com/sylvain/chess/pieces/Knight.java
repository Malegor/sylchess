package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class Knight extends PieceOnBoard {

    public Knight(Color color, Square square) {
        super(color, square);
    }

    @Override
    public Set<Square> getControlledSquares(ChessBoard board) {
        final List<Integer> knightJump = List.of(-2, -1, 1, 2);
        return knightJump.stream()
                .flatMap(i -> knightJump.stream()
                        .filter(j -> Math.abs(i) != Math.abs(j))
                        .map(j -> square.move(i, j))
                        .filter(ChessBoard::isInBoard))
                .collect(Collectors.toSet());
    }

    @Override
    public char printOnBoard() {
        return this.color == Color.WHITE ? 'N' : 'n';
    }
}
