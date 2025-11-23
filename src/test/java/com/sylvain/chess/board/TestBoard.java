package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.pieces.PieceKind;
import org.junit.Test;

import java.util.Map;

public class TestBoard {

    @Test
    public void testBoard() {
        final ChessBoard board = new ChessBoard();
        final Map<Square, PieceKind> whitePieces = Map.of(new Square(1, 1), PieceKind.ROOK);
        final Map<Square, PieceKind> blackPieces = Map.of(new Square(5, 8), PieceKind.KING);
        board.addPieces(Color.WHITE, whitePieces, Color.BLACK, blackPieces);
        board.printBoard();
    }

    @Test
    public void testClassicalBoard() {
        final ChessBoard board = ChessBoard.startingPositions();
        board.printBoard();
    }
}
