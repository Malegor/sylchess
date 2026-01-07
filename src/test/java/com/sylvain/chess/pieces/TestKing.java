package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

@Log4j2
public class TestKing {
    @Test
    public void testControlledSquares() {
        int totalSquares = 0;
        for (int i = 1; i <= ChessBoard.BOARD_COLS; i++) {
            for (int j = 1; j <= ChessBoard.BOARD_ROWS; j++) {
                final Square startingSquare = new Square(i, j);
                final List<Square> squares = new King(Color.WHITE, startingSquare).getControlledSquares(new ChessBoard());
                System.out.println(startingSquare + " : " + squares);
                for (Square square : squares) {
                    Assert.assertTrue(ChessBoard.isInBoard(square));
                    Assert.assertTrue(Math.abs(square.column() - startingSquare.column()) <= 1);
                    Assert.assertTrue(Math.abs(square.row() - startingSquare.row()) <= 1);
                    Assert.assertTrue(square.column() != startingSquare.column() || square.row() != startingSquare.row());
                }
                totalSquares += squares.size();
            }
        }
      log.info("Total controlled: {}", totalSquares);
    }
}
