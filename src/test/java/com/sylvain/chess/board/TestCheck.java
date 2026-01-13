package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.pieces.*;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

public class TestCheck {

    @Test
    public void testClassicalBoard() {
        final ChessBoard board = ChessBoard.defaultBoard();
        board.printBoard();
        Assert.assertTrue(board.piecesCheckingKing(Color.WHITE).isEmpty());
        Assert.assertTrue(board.piecesCheckingKing(Color.BLACK).isEmpty());
    }

    @Test
    public void testNoCheck() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new Rook(Color.WHITE, new Square(1, 1)));
        board.addPiece(new Rook(Color.BLACK, new Square(5, 1)));
        board.addPiece(new King(Color.BLACK, new Square(5, 8)));
        board.printBoard();
        Assert.assertTrue(board.piecesCheckingKing(Color.BLACK).isEmpty());
    }

    @Test
    public void testSingleCheck() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new Rook(Color.WHITE, new Square(1, 1)));
        board.addPiece(new Rook(Color.BLACK, new Square(5, 1)));
        board.addPiece(new King(Color.WHITE, new Square(5, 8)));
        board.printBoard();
        Assert.assertEquals(1, board.piecesCheckingKing(Color.WHITE).size());
        Assert.assertEquals("re1", board.piecesCheckingKing(Color.WHITE).getFirst().toString());
    }

    @Test
    public void testPiecePreventingCheck() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new Rook(Color.WHITE, new Square(5, 5)));
        board.addPiece(new Rook(Color.BLACK, new Square(5, 1)));
        board.addPiece(new King(Color.WHITE, new Square(5, 8)));
        board.printBoard();
        Assert.assertTrue(board.piecesCheckingKing(Color.WHITE).isEmpty());
    }

    @Test
    public void testSeveralChecks() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new King(Color.BLACK, new Square(5, 5)));
        board.addPiece(new Knight(Color.BLACK, new Square(6, 6)));
        board.addPiece(new Queen(Color.WHITE, new Square(7, 7)));
        board.addPiece(new Knight(Color.WHITE, new Square(6, 7))); // check!
        board.addPiece(new Queen(Color.WHITE, new Square(5, 8))); // check!
        board.addPiece(new Rook(Color.WHITE, new Square(5, 1)));
        board.addPiece(new Pawn(Color.WHITE, new Square(4, 4))); // check!
        board.addPiece(new Pawn(Color.WHITE, new Square(5, 4)));
        board.addPiece(new Pawn(Color.WHITE, new Square(4, 6)));
        board.addPiece(new Bishop(Color.WHITE, new Square(7, 3))); // check!
        board.printBoard();
        Assert.assertEquals(4, board.piecesCheckingKing(Color.BLACK).size());
        Assert.assertEquals(Set.of("Qe8", "Bg3", "Pd4", "Nf7"), board.piecesCheckingKing(Color.BLACK).stream().map(PieceOnBoard::toString).collect(Collectors.toSet()));
    }
}
