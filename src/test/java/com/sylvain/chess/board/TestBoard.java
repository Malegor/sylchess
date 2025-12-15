package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Rook;
import org.junit.Test;

public class TestBoard {

    @Test
    public void testBoard() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new Rook(Color.WHITE, new Square(1, 1)));
        board.addPiece(new King(Color.BLACK, new Square(5, 8)));
        board.printBoard();
    }

    @Test
    public void testClassicalBoard() {
        final ChessBoard board = ChessBoard.startingPositions();
        board.printBoard();
    }
}
