package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.moves.Move;
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
                Assert.assertEquals(1, square.row() - startingSquare.row());
                Assert.assertEquals(1, Math.abs(square.column() - startingSquare.column()));
            }
        }
    }

    @Test
    public void testPossibleCaptures() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new Pawn(Color.WHITE, new Square(4, 4)));
        board.addPiece(new Pawn(Color.BLACK, new Square(3, 6)));
        board.addPiece(new Pawn(Color.BLACK, new Square(4, 6)));
        board.addPiece(new Pawn(Color.BLACK, new Square(5, 6)));
        board.addPiece(new Pawn(Color.BLACK, new Square(3, 5)));
        board.addPiece(new Pawn(Color.BLACK, new Square(4, 5)));
        board.addPiece(new Pawn(Color.BLACK, new Square(5, 5)));
        final List<Move> allValidMoves = board.getAllValidMoves(Color.WHITE);
        System.out.println(allValidMoves);
        Assert.assertEquals(2, allValidMoves.size());
    }

    @Test
    public void testPossibleCapturesBlack() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new Pawn(Color.BLACK, new Square(4, 4)));
        board.addPiece(new Pawn(Color.WHITE, new Square(3, 2)));
        board.addPiece(new Pawn(Color.WHITE, new Square(4, 2)));
        board.addPiece(new Pawn(Color.WHITE, new Square(5, 2)));
        board.addPiece(new Pawn(Color.WHITE, new Square(3, 3)));
        board.addPiece(new Pawn(Color.WHITE, new Square(4, 3)));
        board.addPiece(new Pawn(Color.WHITE, new Square(5, 3)));
        final List<Move> allValidMoves = board.getAllValidMoves(Color.BLACK);
        System.out.println(allValidMoves);
        Assert.assertEquals(2, allValidMoves.size());
    }
}
