package com.sylvain.chess.board;

import com.sylvain.chess.Constants;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

public class TestPieceOnBoard {
    @Test
    public void testBoardLimits() {
        Assert.assertFalse((new PieceOnBoard(new King(), 0, 5)).isValid());
        Assert.assertFalse((new PieceOnBoard(new Rook(), 2, Constants.BOARD_ROWS + 1)).isValid());
        Assert.assertTrue((new PieceOnBoard(new Queen(), 1, 1)).isValid());
    }

    @Test
    public void testPawnPosition() {
        Assert.assertTrue(new PieceOnBoard(new Pawn(), 1, 5).isValid());
        Assert.assertTrue(new PieceOnBoard(new Pawn(), Constants.BOARD_COLS, 5).isValid());
        Assert.assertFalse(new PieceOnBoard(new Pawn(), 2, 1).isValid());
        Assert.assertFalse(new PieceOnBoard(new Pawn(), 2, Constants.BOARD_COLS).isValid());
    }
}
