package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import org.junit.Test;

public class TestValidMoves {
    @Test
    public void testClassicalBoard() {
        final ChessBoard board = ChessBoard.startingPositions();
        board.printBoard();
        System.out.println(board.getAllValidMoves(Color.WHITE));
    }
}
