package com.sylvain.chess.board;

import com.sylvain.chess.Constants;
import com.sylvain.chess.pieces.*;
import org.junit.Assert;
import org.junit.Test;

public class TestPieceOnBoard {
    @Test
    public void testBoardLimits() {
        Assert.assertFalse((new PieceOnBoard(Piece.KING, new Square(0, 5))).isValid());
        Assert.assertFalse((new PieceOnBoard(Piece.QUEEN, new Square(2, Constants.BOARD_ROWS + 1))).isValid());
        Assert.assertTrue((new PieceOnBoard(Piece.ROOK, new Square(1, 1))).isValid());
    }

    @Test
    public void testPawnPosition() {
        Assert.assertTrue(new PieceOnBoard(Piece.PAWN, new Square(1, 5)).isValid());
        Assert.assertTrue(new PieceOnBoard(Piece.PAWN, new Square(Constants.BOARD_COLS, 5)).isValid());
        Assert.assertFalse(new PieceOnBoard(Piece.PAWN, new Square(2, 1)).isValid());
        Assert.assertFalse(new PieceOnBoard(Piece.PAWN, new Square(2, Constants.BOARD_ROWS)).isValid());
    }
}
