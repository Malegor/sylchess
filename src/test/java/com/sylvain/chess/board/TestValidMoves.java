package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.Bishop;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import com.sylvain.chess.play.players.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TestValidMoves {
    @Test
    public void testStartingPositions() {
        final ChessBoard board = ChessBoard.startingPositions();
        board.printBoard();
        System.out.println(board.getPositionString());
        final List<Move> allValidMovesForWhite = board.findAllValidMoves(Color.WHITE);
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
      final List<Move> allValidMovesForBlack = board.findAllValidMoves(Color.BLACK);
      System.out.println(allValidMovesForBlack);
      // 3 possible moves for the pawn, each with 4 possible promotions
      Assert.assertEquals(4 * 3, allValidMovesForBlack.size());
      final List<Move> allValidMovesForWhite = board.findAllValidMoves(Color.WHITE);
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
      final List<Move> allValidMovesForBlack = board.findAllValidMoves(Color.BLACK);
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
    final List<Move> allValidMoves = board.findAllValidMoves(Color.WHITE);
    System.out.println(allValidMoves);
    Assert.assertEquals(Set.of("Move{Be2=Bd1}", "Move{Be2=Bf1}"), allValidMoves.stream().map(Move::toString).collect(Collectors.toSet()));
  }

  @Test
  public void testCheckmate() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new Pawn(Color.WHITE, new Square(1,2)));
    board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
    board.addPiece(new Rook(Color.BLACK, new Square(8,1)));
    board.printBoard();
    final List<Move> allValidMoves = board.findAllValidMoves(Color.WHITE);
    System.out.println(allValidMoves);
    Assert.assertTrue(allValidMoves.isEmpty());
  }

  @Test
  public void testStalemate() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
    board.addPiece(new Pawn(Color.BLACK, new Square(3,2)));
    board.addPiece(new Pawn(Color.BLACK, new Square(2,3)));
    board.printBoard();
    final List<Move> allValidMoves = board.findAllValidMoves(Color.WHITE);
    System.out.println(allValidMoves);
    Assert.assertTrue(allValidMoves.isEmpty());
  }

  @Test
  public void testEnPassant() {
    final ChessBoard board = new ChessBoard();
    final Pawn whitePawn = new Pawn(Color.WHITE, new Square(2, 2));
    board.addPiece(whitePawn);
    final Pawn blackPawn = new Pawn(Color.BLACK, new Square(3, 4));
    board.addPiece(blackPawn);
    board.printBoard();
    final Move previousMove = new Move(Map.of(whitePawn, new Pawn(whitePawn.getColor(), whitePawn.getSquare().move(0, 2))), board);
    previousMove.apply();
    board.printBoard();
    board.setPreviousMove(previousMove);
    final List<Move> blackMoves = board.findAllValidMoves(Color.BLACK);
    System.out.println(blackMoves);
    Assert.assertEquals(2, blackMoves.size());
    Assert.assertTrue(blackMoves.stream().map(Move::toString).collect(Collectors.toSet()).contains("Move{pc4=pb3}"));
    final Move enPassant = blackMoves.stream().filter(m -> m.toString().contains("pb3")).findFirst().orElse(null);
    Assert.assertNotNull(enPassant);
    enPassant.apply();
    board.printBoard();
  }

  @Test
  public void testCastle() {
      final ChessBoard board = new ChessBoard();
      board.addPiece(new King(Color.WHITE, new Square(5, 1)));
    board.addPiece(new Rook(Color.WHITE, new Square(1, 1)));
    board.addPiece(new Rook(Color.WHITE, new Square(8, 1)));
    board.printBoard();
    final Set<Move> castles = board.findAllValidMoves(Color.WHITE).stream().filter(move -> move.getMoveToNewSquare().size() > 1).collect(Collectors.toSet());
    System.out.println(castles);
    Assert.assertEquals(2, castles.size());
    board.addPiece(new Queen(Color.BLACK, new Square(8, 4)));
    board.printBoard();
    final Set<Move> castlesWithCheck = board.findAllValidMoves(Color.WHITE).stream().filter(move -> move.getMoveToNewSquare().size() > 1).collect(Collectors.toSet());
    System.out.println(castlesWithCheck);
    Assert.assertTrue(castlesWithCheck.isEmpty());
    board.addPiece(new Pawn(Color.WHITE, new Square(7, 3)));
    board.addPiece(new Pawn(Color.BLACK, new Square(1, 2)));
    board.printBoard();
    final Set<Move> castlesNotThreatened = board.findAllValidMoves(Color.WHITE).stream().filter(move -> move.getMoveToNewSquare().size() > 1).collect(Collectors.toSet());
    System.out.println(castlesNotThreatened);
    Assert.assertEquals(2, castlesNotThreatened.size());
    board.addPiece(new Pawn(Color.BLACK, new Square(7, 2)));
    board.printBoard();
    final Set<Move> castleOnlyOne = board.findAllValidMoves(Color.WHITE).stream().filter(move -> move.getMoveToNewSquare().size() > 1).collect(Collectors.toSet());
    System.out.println(castleOnlyOne);
    Assert.assertEquals(1, castleOnlyOne.size());
    board.addPiece(new Bishop(Color.WHITE, new Square(2, 1)));
    board.printBoard();
    final Set<Move> castlesNotAnyMore = board.findAllValidMoves(Color.WHITE).stream().filter(move -> move.getMoveToNewSquare().size() > 1).collect(Collectors.toSet());
    System.out.println(castlesNotAnyMore);
    Assert.assertTrue(castlesNotAnyMore.isEmpty());
  }

  @Test
  public void testInvalidMove() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new King(Color.BLACK, new Square(8,8)));
    final Player player = new Player(Color.WHITE, board) {
      @Override
      protected Move selectMove(final List<Move> validMoves) {
        final King king = board.getKing(Color.WHITE);
        return new Move(Map.of(king, king.move(2, 1)), board);
      }
    };
    Assert.assertThrows(IllegalArgumentException.class, player::move);
  }
}
