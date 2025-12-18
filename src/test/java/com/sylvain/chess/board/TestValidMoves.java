package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.Bishop;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

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

    @Test
    public void testAvoidCheck() {
      final ChessBoard board = new ChessBoard();
      board.addPiece(new King(Color.BLACK, new Square(1,2)));
      board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
      board.addPiece(new Rook(Color.WHITE, new Square(8,1)));
      board.printBoard();
      final Set<Move> allValidMovesForBlack = board.getAllValidMoves(Color.BLACK);
      System.out.println(allValidMovesForBlack);
      Assert.assertEquals(Set.of("Move{ka2=kb3}", "Move{ka2=kb2}"), allValidMovesForBlack.stream().map(Move::toString).collect(Collectors.toSet()));
    }

  @Test
  public void testPreventCheckMovingPiece() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new Pawn(Color.WHITE, new Square(1,2)));
    board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
    board.addPiece(new Bishop(Color.WHITE, new Square(5,2)));
    board.addPiece(new Rook(Color.BLACK, new Square(8,1)));
    board.printBoard();
    final Set<Move> allValidMoves = board.getAllValidMoves(Color.WHITE);
    System.out.println(allValidMoves);
    Assert.assertEquals(Set.of("Move{Be2=Bd1}", "Move{Be2=Bf1}"), allValidMoves.stream().map(Move::toString).collect(Collectors.toSet()));
  }

  @Test
  public void testCheckMate() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new Pawn(Color.WHITE, new Square(1,2)));
    board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
    board.addPiece(new Rook(Color.BLACK, new Square(8,1)));
    board.printBoard();
    final Set<Move> allValidMoves = board.getAllValidMoves(Color.WHITE);
    System.out.println(allValidMoves);
    Assert.assertTrue(allValidMoves.isEmpty());
  }

  @Test
  public void testStaleMate() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
    board.addPiece(new Pawn(Color.BLACK, new Square(3,2)));
    board.addPiece(new Pawn(Color.BLACK, new Square(2,3)));
    board.printBoard();
    final Set<Move> allValidMoves = board.getAllValidMoves(Color.WHITE);
    System.out.println(allValidMoves);
    Assert.assertTrue(allValidMoves.isEmpty());
  }
}
