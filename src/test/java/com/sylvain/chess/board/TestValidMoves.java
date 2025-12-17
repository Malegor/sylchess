package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.Bishop;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.Queen;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;

public class TestValidMoves {
    @Test
    public void testStartingPositions() {
        final ChessBoard board = ChessBoard.startingPositions();
        board.printBoard();
        final Set<Move> allValidMovesForWhite = board.getAllValidMoves(Color.WHITE);
        System.out.println(allValidMovesForWhite);
        // 2 moves for each knight and 2 moves for each pawn.
        Assert.assertEquals(2 * 2 + 2 * 8, allValidMovesForWhite.size());
    }

    @Test
    public void testPromotion() {
      final ChessBoard board = new ChessBoard();
      board.addPiece(new Pawn(Color.BLACK, new Square(5, 2)));
      board.addPiece(new Bishop(Color.WHITE, new Square(6, 1)));
      board.addPiece(new Queen(Color.WHITE, new Square(4, 1)));
      board.printBoard();
      final Set<Move> allValidMovesForBlack = board.getAllValidMoves(Color.BLACK);
      System.out.println(allValidMovesForBlack);
      // 3 possible moves for the pawn, each with 4 possible promotions
      Assert.assertEquals(4 * 3, allValidMovesForBlack.size());
      final Set<Move> allValidMovesForWhite = board.getAllValidMoves(Color.WHITE);
      System.out.println(allValidMovesForWhite);
      // 3 possible bishop moves, and for the queen 7 vertical, 4 horizontal and 4 diagonal.
      Assert.assertEquals(3 + (7+4+4), allValidMovesForWhite.size());
    }
}
