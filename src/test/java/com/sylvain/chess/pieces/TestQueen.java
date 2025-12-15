package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

@Log
public class TestQueen {
    @Test
    public void testControlledSquares() {
        int totalSquares = 0;
        for (int i = 1; i <= ChessBoard.BOARD_COLS; i++) {
            for (int j = 1; j <= ChessBoard.BOARD_ROWS; j++) {
                final Square startingSquare = new Square(i, j);
                final Set<Square> squares = new Queen(Color.WHITE, startingSquare).getControlledSquares(null);
                System.out.println(startingSquare + " : " + squares);
                final Set<Square> rookControl = new Rook(Color.WHITE, startingSquare).getControlledSquares(null);
                final Set<Square> bishopControl = new Bishop(Color.WHITE, startingSquare).getControlledSquares(null);
                Assert.assertEquals(rookControl.size() + bishopControl.size(), squares.size());
                Assert.assertTrue(squares.containsAll(rookControl));
                Assert.assertTrue(squares.containsAll(bishopControl));
                for (Square square : squares) {
                    Assert.assertTrue(ChessBoard.isInBoard(square));
                }
                totalSquares += squares.size();
            }
        }
        log.info("Total controlled: " + totalSquares);
    }
}
