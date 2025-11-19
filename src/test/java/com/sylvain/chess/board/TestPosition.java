package com.sylvain.chess.board;

import com.sylvain.chess.pieces.King;
import org.junit.Assert;
import org.junit.Test;

public class TestPosition {
    @Test
    public void testOutOfBoard() {
        Assert.assertFalse((new Position(new King(), 0, 5)).isValid());
    }
}
