package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.ArrayList;
import java.util.List;

public class King extends PieceOnBoard {
    public King(final Color color, final Square square) {
        this(color, square, false);
    }

    public King(final Color color, final Square square, final boolean hasAlreadyMoved) {
        super(color, square, hasAlreadyMoved);
    }

    @Override
    public King at(final Square square) {
        return new King(this.color, square, this.hasAlreadyMoved);
    }

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        final List<Square> controlled = new ArrayList<>(8);
        final List<Integer> neighborhood = List.of(-1,0,1);
        for (int i : neighborhood) {
            for (int j : neighborhood) {
                final Square result = this.square.move(i, j);
                if ((i != 0 || j != 0) && ChessBoard.isInBoard(result)) {
                    controlled.add(result);
                }
            }
        }
        return controlled;
    }

    @Override
    public char printOnBoard() {
        return this.color == Color.WHITE ? 'K' : 'k';
    }

    @Override
    public boolean isPossiblePromotion() {
        return false;
    }
}
