package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@Log
public class TestBishop {
    @Test
    public void testControlledSquares() {
        int totalSquares = 0;
        for (int i = 1; i <= ChessBoard.BOARD_COLS; i++) {
            for (int j = 1; j <= ChessBoard.BOARD_ROWS; j++) {
                final Square startingSquare = new Square(i, j);
                final List<Square> squares = new Bishop(Color.WHITE, startingSquare).getControlledSquares(null);
                System.out.println(startingSquare + " : " + squares);
                Assert.assertTrue(squares.size() >= 7 && squares.size() <= 13);
                for (Square square : squares) {
                    Assert.assertTrue(ChessBoard.isInBoard(square));
                    Assert.assertEquals(Math.abs(square.getColumn() - i), Math.abs(square.getRow() - j));
                }
                totalSquares += squares.size();
            }
        }
        log.info("Total controlled: " + totalSquares);
    }
}
