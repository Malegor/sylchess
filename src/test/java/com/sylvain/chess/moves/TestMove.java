package com.sylvain.chess.moves;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.Bishop;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Knight;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

public class TestMove {
  @Test
  public void testToSan() {
    final ChessBoard board = ChessBoard.emptyBoard();
    final Knight knight = new Knight(PlayerColor.BLACK, new Square(1, 1));
    board.addPiece(knight);
    board.addPiece(new Knight(PlayerColor.BLACK, new Square(3, 2))); // Doesn't control b3
    board.addPiece(new Queen(PlayerColor.BLACK, new Square(2, 8))); // Isn't a knight
    System.out.println(new Move(Map.of(knight, knight.move(1, 2)), board).toSan());
    Assert.assertEquals("Nb3", new Move(Map.of(knight, knight.move(1, 2)), board).toSan());
    board.addPiece(new Knight(PlayerColor.BLACK, new Square(4, 2)));
    Assert.assertEquals("Nab3", new Move(Map.of(knight, knight.move(1, 2)), board).toSan());
    board.addPiece(new Knight(PlayerColor.BLACK, new Square(4, 4)));
    Assert.assertEquals("Nab3", new Move(Map.of(knight, knight.move(1, 2)), board).toSan());
    board.addPiece(new Knight(PlayerColor.BLACK, new Square(1, 5)));
    Assert.assertEquals("N1b3", new Move(Map.of(knight, knight.move(1, 2)), board).toSan());
    board.addPiece(new Knight(PlayerColor.BLACK, new Square(3, 1)));
    Assert.assertEquals("Na1b3", new Move(Map.of(knight, knight.move(1, 2)), board).toSan());
    board.addPiece(new Bishop(PlayerColor.WHITE, new Square(2, 3)));
    Assert.assertEquals("Na1xb3", new Move(Map.of(knight, knight.move(1, 2)), board).toSan());
    final Pawn whitePawn = new Pawn(PlayerColor.WHITE, new Square(3, 7));
    board.addPiece(whitePawn);
    final Move pawnPromo = new Move(Map.of(whitePawn, whitePawn.toBishop(whitePawn.getSquare().move(0, 1))), board);
    Assert.assertEquals("c8=B", pawnPromo.toSan());
    final Move pawnPromoOnCapture = new Move(Map.of(whitePawn, whitePawn.toQueen(whitePawn.getSquare().move(-1, 1))), board);
    Assert.assertEquals("cxb8=Q", pawnPromoOnCapture.toSan());
    board.addPiece(new Pawn(PlayerColor.WHITE, new Square(1, 7)));
    Assert.assertEquals("cxb8=Q", pawnPromoOnCapture.toSan()); // No disambiguating is necessary
    final King whiteKing = new King(PlayerColor.WHITE, new Square(7, 1));
    final Rook whiteRook = new Rook(PlayerColor.WHITE, new Square(8, 1));
    final Move castling = board.getCastleMove(whiteKing, whiteRook);
    Assert.assertNotNull(castling);
    Assert.assertTrue(castling.isValidMove());
    Assert.assertEquals("O-O", castling.toSan()); // King side
    final Rook otherWhiteRook = new Rook(PlayerColor.WHITE, new Square(8, 2));
    board.addPiece(otherWhiteRook);
    final Move rookMovesOnSameColumn = new Move(Map.of(otherWhiteRook, otherWhiteRook.move(0, 1)), board);
    Assert.assertEquals("Rh3", rookMovesOnSameColumn.toSan());
    board.printBoard();
  }
}
