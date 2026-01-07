package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@Log4j2
public class TestRook {
    @Test
    public void testControlledSquares() {
        int totalSquares = 0;
        for (int i = 1; i <= ChessBoard.BOARD_COLS; i++) {
            for (int j = 1; j <= ChessBoard.BOARD_ROWS; j++) {
                final Square startingSquare = new Square(i, j);
                final List<Square> squares = new Rook(Color.WHITE, startingSquare).getControlledSquares(null);
                System.out.println(startingSquare + " : " + squares);
                Assert.assertEquals(14, squares.size());
                for (Square square : squares) {
                    Assert.assertTrue(ChessBoard.isInBoard(square));
                    Assert.assertTrue(square.column() == i ^ square.row() == j);
                }
                totalSquares += squares.size();
            }
        }
        log.info("Total controlled: " + totalSquares);
    }
}
