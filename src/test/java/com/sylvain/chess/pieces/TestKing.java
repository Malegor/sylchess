package com.sylvain.chess.pieces;

import com.sylvain.chess.Constants;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.extern.java.Log;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

@Log
public class TestKing {
    @Test
    public void testControlledSquares() {
        int totalSquares = 0;
        for (int i = 1; i <= Constants.BOARD_COLS; i++) {
            for (int j = 1; j <= Constants.BOARD_ROWS; j++) {
                final Square startingSquare = new Square(i, j);
                final Set<Square> squares = PieceKind.KING.getControlledSquares(startingSquare);
                System.out.println(startingSquare + " : " + squares);
                for (Square square : squares) {
                    Assert.assertTrue(ChessBoard.isInBoard(square));
                    Assert.assertTrue(Math.abs(square.getColumn() - startingSquare.getColumn()) <= 1);
                    Assert.assertTrue(Math.abs(square.getRow() - startingSquare.getRow()) <= 1);
                    Assert.assertTrue(square.getColumn() != startingSquare.getColumn() || square.getRow() != startingSquare.getRow());
                }
                totalSquares += squares.size();
            }
        }
        log.info("Total controlled: " + totalSquares);
    }
}
