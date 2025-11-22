package com.sylvain.chess.board;

import com.sylvain.chess.Constants;
import com.sylvain.chess.pieces.*;
import org.junit.Assert;
import org.junit.Test;

public class TestPieceOnBoard {
    @Test
    public void testBoardLimits() {
        Assert.assertFalse(Piece.KING.isValidAt(new Square(0, 5)));
        Assert.assertFalse(Piece.QUEEN.isValidAt(new Square(2, Constants.BOARD_ROWS + 1)));
        Assert.assertTrue(Piece.ROOK.isValidAt(new Square(1, 1)));
    }

    @Test
    public void testPawnPosition() {
        Assert.assertTrue(Piece.PAWN.isValidAt(new Square(1, 5)));
        Assert.assertTrue(Piece.PAWN.isValidAt(new Square(Constants.BOARD_COLS, 5)));
        Assert.assertFalse(Piece.PAWN.isValidAt(new Square(2, 1)));
        Assert.assertFalse(Piece.PAWN.isValidAt(new Square(2, Constants.BOARD_ROWS)));
    }
}
