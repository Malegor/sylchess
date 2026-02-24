package com.sylvain.chess.pieces;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.List;

public class NoPiece extends PieceOnBoard {
    public static char NAME = ' ';

    public NoPiece(final PlayerColor color, final Square square) {
        super(color, square, false);
    }

    @Override
    public NoPiece at(final Square square) {
        return new NoPiece(this.color, square);
    }

    @Override
    public String getIconPath(PlayerColor color) {
        return "";
    }

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        return List.of();
    }

    @Override
    public Character getName() {
        return NAME;
    }

    @Override
    public boolean isPossiblePromotion() {
        return false;
    }
}
