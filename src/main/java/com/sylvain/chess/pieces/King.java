package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class King extends PieceOnBoard {

    public King(Color color, Square square) {
        super(color, square);
    }

    @Override
    public Set<Square> getControlledSquares(ChessBoard board) {
        final Set<Square> controlled = new HashSet<>();
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
}
