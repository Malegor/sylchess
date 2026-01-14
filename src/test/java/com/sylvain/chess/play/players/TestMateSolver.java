package com.sylvain.chess.play.players;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.moves.Move;
import org.junit.Assert;
import org.junit.Test;

public class TestMateSolver {
  @Test
  public void testMatDuBerger() {
    final ChessBoard board = FenLoader.loadBoard("r1bqk1nr/pppp1ppp/2n5/2b1p3/2B1P3/5Q2/PPPP1PPP/RNB1K1NR");
    final MateSolver mateIn1 = new MateSolver(Color.WHITE, board, 1);
    final Move mateMove = mateIn1.selectMove(board.findAllValidMoves(Color.WHITE));
    System.out.println(mateMove);
    Assert.assertEquals("Qxf7", mateMove.toPgn());
  }

  @Test
  public void testMatDeLimbecile() {
    final ChessBoard board = FenLoader.loadBoard("rnbqkbnr/pppp1ppp/8/4p3/5PP1/8/PPPPP2P/RNBQKBNR");
    final MateSolver mateIn1 = new MateSolver(Color.BLACK, board, 1);
    final Move mateMove = mateIn1.selectMove(board.findAllValidMoves(Color.BLACK));
    System.out.println(mateMove);
    Assert.assertEquals("Qh4", mateMove.toPgn());
  }
}
