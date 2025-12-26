package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;

import java.util.ArrayList;
import java.util.List;

public class Queen extends PieceOnBoard {
    public Queen(Color color, Square square) {
        super(color, square);
    }

    @Override
    public List<Square> getControlledSquares(ChessBoard board) {
        final List<Square> controlled = new ArrayList<>(14);
        controlled.addAll(new Bishop(this.color, this.square).getControlledSquares(board));
        controlled.addAll(new Rook(this.color, this.square).getControlledSquares(board));
        return controlled;
    }

    @Override
    public char printOnBoard() {
        return this.color == Color.WHITE ? 'Q' : 'q';
    }

    @Override
    public boolean isPossiblePromotion() {
        return true;
    }

    @Override
    public Queen at(Square square) {
        return new Queen(this.color, square);
    }
}
