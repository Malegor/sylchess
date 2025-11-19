package com.sylvain.chess.board;

import com.sylvain.chess.Constants;
import com.sylvain.chess.pieces.*;
import org.junit.Assert;
import org.junit.Test;

public class TestPieceOnBoard {
    @Test
    public void testBoardLimits() {
        Assert.assertFalse((new PieceOnBoard(Piece.KING, 0, 5)).isValid());
        Assert.assertFalse((new PieceOnBoard(Piece.QUEEN, 2, Constants.BOARD_ROWS + 1)).isValid());
        Assert.assertTrue((new PieceOnBoard(Piece.ROOK, 1, 1)).isValid());
    }

    @Test
    public void testPawnPosition() {
        Assert.assertTrue(new PieceOnBoard(Piece.PAWN, 1, 5).isValid());
        Assert.assertTrue(new PieceOnBoard(Piece.PAWN, Constants.BOARD_COLS, 5).isValid());
        Assert.assertFalse(new PieceOnBoard(Piece.PAWN, 2, 1).isValid());
        Assert.assertFalse(new PieceOnBoard(Piece.PAWN, 2, Constants.BOARD_ROWS).isValid());
    }
}
