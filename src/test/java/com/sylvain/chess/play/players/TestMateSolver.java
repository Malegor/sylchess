package com.sylvain.chess.play.players;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.moves.EvaluatedMove;
import com.sylvain.chess.moves.Move;
import org.junit.Assert;
import org.junit.Test;

public class TestMateSolver {
  @Test
  public void testMatDuBerger() {
    final ChessBoard board = FenLoader.loadBoard("r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR");
    final MateSolver mateIn1 = new MateSolver(PlayerColor.WHITE, board, 3);
    final Move mateMove = mateIn1.selectMove(board.findAllValidMoves(PlayerColor.WHITE));
    board.printBoard();
    System.out.println(mateMove);
    Assert.assertEquals("Qxf7", mateMove.toSan());
  }

  @Test
  public void testMatDeLimbecile() {
    final ChessBoard board = FenLoader.loadBoard("rnbqkbnr/pppp1ppp/8/4p3/5PP1/8/PPPPP2P/RNBQKBNR");
    final MateSolver mateIn1 = new MateSolver(PlayerColor.BLACK, board, 3);
    final Move mateMove = mateIn1.selectMove(board.findAllValidMoves(PlayerColor.BLACK));
    board.printBoard();
    System.out.println(mateMove);
    Assert.assertEquals("Qh4", mateMove.toSan());
  }

  @Test
  public void testMateIn2() {
    final ChessBoard board = FenLoader.loadBoard("1k6/pnp1N3/1pP3p1/4b2p/1N5P/P5P1/1P3qBK/8");
    final MateSolver mateIn2 = new MateSolver(PlayerColor.WHITE, board, 3);
    final Move mateMove = mateIn2.selectMove(board.findAllValidMoves(PlayerColor.WHITE));
    board.printBoard();
    System.out.println(mateMove);
    Assert.assertEquals("Na6", mateMove.toSan());
  }

  @Test
  public void testOtherMateIn2() {
    final ChessBoard board = FenLoader.loadBoard("Q4rkr/1p3p1p/7P/R2Bp3/8/8/4KP1p/8");
    final MateSolver mateIn2 = new MateSolver(PlayerColor.WHITE, board, 3);
    final Move mateMove = mateIn2.selectMove(board.findAllValidMoves(PlayerColor.WHITE));
    board.printBoard();
    System.out.println(mateMove);
    Assert.assertEquals("Bh1", mateMove.toSan());
  }

  @Test
  public void testOther2MateIn2() {
    final ChessBoard board = FenLoader.loadBoard("krN5/1n3p2/2Q1pP2/qN2B1Pp/PpK2p1P/1P3B2/Rp6/bR6");
    final MateSolver mateIn2 = new MateSolver(PlayerColor.WHITE, board, 3);
    final Move mateMove = mateIn2.selectMove(board.findAllValidMoves(PlayerColor.WHITE));
    board.printBoard();
    System.out.println(mateMove);
    Assert.assertEquals("gxh6", mateMove.toSan());
  }

  @Test
  public void testMateIn3() {
    final ChessBoard board = FenLoader.loadBoard("1kr4r/ppp2p2/5bpq/4N3/4PP2/1b4P1/PPP2Q1P/R5K1");
    final MateSolver mateIn2 = new MateSolver(PlayerColor.WHITE, board, 5);
    final Move mateMove = mateIn2.selectMove(board.findAllValidMoves(PlayerColor.WHITE));
    board.printBoard();
    System.out.println(mateMove);
    Assert.assertEquals("Nd7", mateMove.toSan());
  }

  @Test
  public void testMateIn5() {
    final ChessBoard board = FenLoader.loadBoard("Q7/p4ppk/4p3/1q4Pp/2n5/2B5/Pr4PP/K3R2R");
    final MateSolver mateIn2 = new MateSolver(PlayerColor.BLACK, board, 9);
    final Move mateMove = mateIn2.selectMove(board.findAllValidMoves(PlayerColor.BLACK));
    board.printBoard();
    System.out.println(mateMove);
    Assert.assertEquals("Rxa2", mateMove.toSan());
  }

  @Test
  public void testDefendingMateEnPassant() {
    final ChessBoard board = FenLoader.loadBoard("2R4r/5pbk/p3p3/4P1P1/1p2B3/8/8/6K1");
    final MateSolver defMateIn1 = new MateSolver(PlayerColor.BLACK, board, 5);
    final EvaluatedMove defMove = defMateIn1.selectEvaluatedMove(board.findAllValidMoves(PlayerColor.BLACK));
    board.printBoard();
    Assert.assertEquals(-498, defMove.evaluation(), 1e-6);
  }
}
