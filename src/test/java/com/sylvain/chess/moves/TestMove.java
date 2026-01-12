package com.sylvain.chess.moves;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.Bishop;
import com.sylvain.chess.pieces.Knight;
import com.sylvain.chess.pieces.Queen;
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
    final Move move = new Move(Map.of(knight, knight.move(1, 2)), board);
    System.out.println(move.toPgn());
    Assert.assertEquals("Nb3", move.toPgn());
    board.addPiece(new Knight(Color.BLACK, new Square(4, 2)));
    Assert.assertEquals("Nab3", move.toPgn());
    board.addPiece(new Knight(Color.BLACK, new Square(4, 4)));
    Assert.assertEquals("Nab3", move.toPgn());
    board.addPiece(new Knight(Color.BLACK, new Square(1, 5)));
    Assert.assertEquals("N1b3", move.toPgn());
    board.addPiece(new Knight(Color.BLACK, new Square(3, 1)));
    Assert.assertEquals("Na1b3", move.toPgn());
    board.addPiece(new Bishop(Color.WHITE, new Square(2, 3)));
    move.isValidMove();// In order to set the captured piece
    Assert.assertEquals("Na1xb3", move.toPgn());
    board.printBoard();
  }
}
