package com.sylvain.chess.board;

import com.sylvain.chess.Constants;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

public class TestPosition {
    @Test
    public void testBoardLimits() {
        Assert.assertFalse((new Position(new King(), 0, 5)).isValid());
        Assert.assertFalse((new Position(new Rook(), 2, Constants.BOARD_ROWS + 1)).isValid());
        Assert.assertTrue((new Position(new Queen(), 1, 1)).isValid());
    }
}
