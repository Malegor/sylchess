package com.sylvain.chess.board;

import org.junit.Assert;
import org.junit.Test;

public class TestPieceOnBoard {
    @Test
    public void testBoardLimits() {
        Assert.assertFalse(ChessBoard.isInBoard(new Square(0, 5)));
        Assert.assertFalse(ChessBoard.isInBoard(new Square(2, ChessBoard.BOARD_ROWS + 1)));
        Assert.assertTrue(ChessBoard.isInBoard(new Square(1, 1)));
    }

    @Test
    public void testPawnPosition() {
        /*Assert.assertTrue(PieceKind.PAWN.isValidAt(new Square(1, 5)));
        Assert.assertTrue(PieceKind.PAWN.isValidAt(new Square(ChessBoard.BOARD_COLS, 5)));
        Assert.assertFalse(PieceKind.PAWN.isValidAt(new Square(2, 1)));
        Assert.assertFalse(PieceKind.PAWN.isValidAt(new Square(2, ChessBoard.BOARD_ROWS)));*/
    }
}
