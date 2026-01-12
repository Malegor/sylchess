package com.sylvain.chess.moves;

import com.sylvain.chess.Color;
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
  public void testToPgn() {
    final ChessBoard board = new ChessBoard();
    final Knight knight = new Knight(Color.BLACK, new Square(1, 1));
    board.addPiece(knight);
    board.addPiece(new Knight(Color.BLACK, new Square(3, 2))); // Doesn't control b3
    board.addPiece(new Queen(Color.BLACK, new Square(2, 8))); // Isn't a knight
    final Move blackMove = new Move(Map.of(knight, knight.move(1, 2)), board);
    System.out.println(blackMove.toPgn());
    Assert.assertEquals("Nb3", blackMove.toPgn());
    board.addPiece(new Knight(Color.BLACK, new Square(4, 2)));
    Assert.assertEquals("Nab3", blackMove.toPgn());
    board.addPiece(new Knight(Color.BLACK, new Square(4, 4)));
    Assert.assertEquals("Nab3", blackMove.toPgn());
    board.addPiece(new Knight(Color.BLACK, new Square(1, 5)));
    Assert.assertEquals("N1b3", blackMove.toPgn());
    board.addPiece(new Knight(Color.BLACK, new Square(3, 1)));
    Assert.assertEquals("Na1b3", blackMove.toPgn());
    board.addPiece(new Bishop(Color.WHITE, new Square(2, 3)));
    blackMove.isValidMove();// In order to set the captured piece
    Assert.assertEquals("Na1xb3", blackMove.toPgn());
    final Pawn whitePawn = new Pawn(Color.WHITE, new Square(3, 7));
    board.addPiece(whitePawn);
    final Move pawnPromo = new Move(Map.of(whitePawn, whitePawn.toBishop(whitePawn.getSquare().move(0, 1))), board);
    Assert.assertEquals("c8=B", pawnPromo.toPgn());
    final Move pawnPromoOnCapture = new Move(Map.of(whitePawn, whitePawn.toQueen(whitePawn.getSquare().move(-1, 1))), board);
    pawnPromoOnCapture.isValidMove();
    Assert.assertEquals("cxb8=Q", pawnPromoOnCapture.toPgn());
    board.addPiece(new Pawn(Color.WHITE, new Square(1, 7)));
    Assert.assertEquals("cxb8=Q", pawnPromoOnCapture.toPgn()); // No disambiguating is necessary
    final King whiteKing = new King(Color.WHITE, new Square(7, 1));
    final Rook whiteRook = new Rook(Color.WHITE, new Square(8, 1));
    final Move castling = board.getCastleMove(whiteKing, whiteRook);
    Assert.assertNotNull(castling);
    Assert.assertTrue(castling.isValidMove());
    Assert.assertEquals("O-O", castling.toPgn()); // King side
    board.printBoard();
  }
}
