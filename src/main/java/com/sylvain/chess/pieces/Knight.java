package com.sylvain.chess.pieces;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.List;
import java.util.stream.Collectors;

public class Knight extends PieceOnBoard {
    public static char NAME_LC = 'n';

    public Knight(final PlayerColor color, final Square square) {
        this(color, square, false);
    }

    public Knight(final PlayerColor color, final Square square, final boolean hasAlreadyMoved) {
        super(color, square, hasAlreadyMoved);
    }

    @Override
    public Knight at(final Square square) {
        return new Knight(this.color, square, this.hasAlreadyMoved);
    }

    @Override
    public String getIconPath(PlayerColor color) {
        final String colorStr = color.equals(PlayerColor.BLACK) ? "d" : "l";
        return "/pieces_png/Chess_n" + colorStr + "t60.png";
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
    public Character getName() {
        return NAME_LC;
    }

    @Override
    public boolean isPossiblePromotion() {
        return true;
    }
}
