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
        final ChessBoard board = ChessBoard.defaultBoard();
        board.printBoard();
    }

    @Test
    public void testGetSquare() {
        final ChessBoard board = new ChessBoard();
        Assert.assertThrows(IllegalArgumentException.class, () -> board.getSquare(""));
        Assert.assertThrows(IllegalArgumentException.class, () -> board.getSquare("a3a"));
        Assert.assertThrows(IllegalArgumentException.class, () -> board.getSquare("a9"));
        Assert.assertThrows(IllegalArgumentException.class, () -> board.getSquare("j1"));
        Assert.assertEquals(new Square(1, 1),  board.getSquare("a1"));
        Assert.assertEquals(new Square(8, 8),  board.getSquare("h8"));
        Assert.assertEquals(new Square(5, 1),  board.getSquare("e1"));
    }
}
