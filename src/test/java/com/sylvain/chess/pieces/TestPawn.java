package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestPawn {
    @Test
    public void testControlledSquares() {
        final int j = 2;
        for (int i = 1; i <= ChessBoard.BOARD_COLS; i++) {
            final Square startingSquare = new Square(i, j);
            final List<Square> squares = new Pawn(Color.WHITE, startingSquare).getControlledSquares(null);
            System.out.println(startingSquare + " : " + squares);
            Assert.assertEquals((i == 1 || i == ChessBoard.BOARD_COLS) ? 1 : 2, squares.size());
            for (Square square : squares) {
                Assert.assertTrue(ChessBoard.isInBoard(square));
                Assert.assertEquals(1, square.getRow() - startingSquare.getRow());
                Assert.assertEquals(1, Math.abs(square.getColumn() - startingSquare.getColumn()));
            }
        }
    }
}
