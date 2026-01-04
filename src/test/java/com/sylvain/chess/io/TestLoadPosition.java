package com.sylvain.chess.io;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.play.Gameplay;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

public class TestLoadPosition {

  @Test
  public void testLoadStartingPositionsBoard() {
    final ChessBoard chessBoard = FenDecoder.loadBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Ra1;Nb1;Bc1;Qd1;Ke1;Bf1;Ng1;Rh1;Pa2;Pb2;Pc2;Pd2;Pe2;Pf2;Pg2;Ph2;pa7;pb7;pc7;pd7;pe7;pf7;pg7;ph7;ra8;nb8;bc8;qd8;ke8;bf8;ng8;rh8;", chessBoard.getPositionString());
  }

  @Test
  public void testAfterMovingPawnBoard() {
    final ChessBoard chessBoard = FenDecoder.loadBoard("rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR");
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Ra1;Nb1;Bc1;Qd1;Ke1;Bf1;Ng1;Rh1;Pa2;Pb2;Pc2;Pd2;Pf2;Pg2;Ph2;Pe4;pa7;pb7;pc7;pd7;pe7;pf7;pg7;ph7;ra8;nb8;bc8;qd8;ke8;bf8;ng8;rh8;", chessBoard.getPositionString());
  }

  @Test
  public void testMateIn3Board() {
    final ChessBoard chessBoard = FenDecoder.loadBoard("1kr4r/ppp2p2/5bpq/4N3/4PP2/1b4P1/PPP2Q1P/R5K1");
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Ra1;Kg1;Pa2;Pb2;Pc2;Qf2;Ph2;bb3;Pg3;Pe4;Pf4;Ne5;bf6;pg6;qh6;pa7;pb7;pc7;pf7;kb8;rc8;rh8;", chessBoard.getPositionString());
  }

  @Test
  public void testMateIn4Board() {
    final ChessBoard chessBoard = FenDecoder.loadBoard("4q3/1p3R1p/r3r3/4p1kP/p1Pp2P1/1P1P3K/6P1/5R2");
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Rf1;Pg2;Pb3;Pd3;Kh3;pa4;Pc4;pd4;Pg4;pe5;kg5;Ph5;ra6;re6;pb7;Rf7;ph7;qe8;", chessBoard.getPositionString());
  }

  @Test
  public void testLoadStartingPositions() throws IOException {
    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream is = classloader.getResourceAsStream("fen/starting.fen");
    final Gameplay gameplay = FenDecoder.loadPosition(new String(is.readAllBytes(), StandardCharsets.UTF_8));
    gameplay.playGame(0);
    Assert.assertEquals(Color.BLACK, gameplay.getLastPlayer().getColor());
    for (Color color : Set.of(Color.WHITE, Color.BLACK)) {
      Assert.assertFalse(gameplay.getBoard().getKing(color).isHasAlreadyMoved());
      Assert.assertEquals(2, gameplay.getBoard().getUnmovedRooks(color).size());
    }
  }

  @Test
  public void testAfterMovingPawn() throws IOException {
    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream is = classloader.getResourceAsStream("fen/after-pawn.fen");
    final Gameplay gameplay = FenDecoder.loadPosition(new String(is.readAllBytes(), StandardCharsets.UTF_8));
    gameplay.playGame(0);
    Assert.assertEquals(Color.WHITE, gameplay.getLastPlayer().getColor());
    for (Color color : Set.of(Color.WHITE, Color.BLACK)) {
      Assert.assertFalse(gameplay.getBoard().getKing(color).isHasAlreadyMoved());
      Assert.assertEquals(2, gameplay.getBoard().getUnmovedRooks(color).size());
    }
  }

  @Test
  public void testMateIn3() throws IOException {
    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream is = classloader.getResourceAsStream("fen/mate3.fen");
    final Gameplay gameplay = FenDecoder.loadPosition(new String(is.readAllBytes(), StandardCharsets.UTF_8));
    gameplay.playGame(0);
    Assert.assertEquals(Color.BLACK, gameplay.getLastPlayer().getColor());
    for (Color color : Set.of(Color.WHITE, Color.BLACK)) {
      Assert.assertFalse(gameplay.getBoard().getKing(color).isHasAlreadyMoved());
      Assert.assertTrue(gameplay.getBoard().getUnmovedRooks(color).isEmpty());
    }
  }

  @Test
  public void testMateIn4() throws IOException {
    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream is = classloader.getResourceAsStream("fen/mate4.fen");
    final Gameplay gameplay = FenDecoder.loadPosition(new String(is.readAllBytes(), StandardCharsets.UTF_8));
    gameplay.playGame(0);
    Assert.assertEquals(Color.BLACK, gameplay.getLastPlayer().getColor());
    for (Color color : Set.of(Color.WHITE, Color.BLACK)) {
      Assert.assertFalse(gameplay.getBoard().getKing(color).isHasAlreadyMoved());
      Assert.assertTrue(gameplay.getBoard().getUnmovedRooks(color).isEmpty());
    }
  }
}
