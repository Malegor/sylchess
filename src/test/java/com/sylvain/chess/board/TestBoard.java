package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

public class TestBoard {
    @Test
    public void testBoardLimits() {
        Assert.assertFalse(ChessBoard.isInBoard(new Square(0, 5)));
        Assert.assertFalse(ChessBoard.isInBoard(new Square(2, ChessBoard.BOARD_ROWS + 1)));
        Assert.assertTrue(ChessBoard.isInBoard(new Square(1, 1)));
    }

    @Test
    public void testPrintBoard() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new Rook(Color.WHITE, new Square(1, 1)));
        board.addPiece(new King(Color.BLACK, new Square(5, 8)));
        board.printBoard();
    }

    @Test
    public void testPrintClassicalBoard() {
        final ChessBoard board = ChessBoard.startingPositions();
        board.printBoard();
    }
}
