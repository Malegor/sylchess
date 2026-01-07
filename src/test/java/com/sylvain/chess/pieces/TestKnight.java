package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@Log4j2
public class TestKnight {
    @Test
    public void testControlledSquares() {
        int totalSquares = 0;
        for (int i = 1; i <= ChessBoard.BOARD_COLS; i++) {
            for (int j = 1; j <= ChessBoard.BOARD_ROWS; j++) {
                final Square startingSquare = new Square(i, j);
                final List<Square> squares = new Knight(Color.WHITE, startingSquare).getControlledSquares(null);
                System.out.println(startingSquare + " : " + squares);
                Assert.assertTrue(squares.size() >= 2 && squares.size() <= 8);
                for (Square square : squares) {
                    Assert.assertTrue(ChessBoard.isInBoard(square));
                    Assert.assertEquals(3, Math.abs(square.column() - i) + Math.abs(square.row() - j));
                }
                totalSquares += squares.size();
            }
        }
        log.info("Total controlled: " + totalSquares);
    }
}
