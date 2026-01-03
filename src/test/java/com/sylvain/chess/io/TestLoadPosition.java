package com.sylvain.chess.io;

import com.sylvain.chess.board.ChessBoard;
import org.junit.Assert;
import org.junit.Test;

public class TestLoadPosition {

  @Test
  public void testLoadStartingPositions() {
    final ChessBoard chessBoard = FenDecoder.loadBoard("rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR");
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Ra1;Nb1;Bc1;Qd1;Ke1;Bf1;Ng1;Rh1;Pa2;Pb2;Pc2;Pd2;Pe2;Pf2;Pg2;Ph2;pa7;pb7;pc7;pd7;pe7;pf7;pg7;ph7;ra8;nb8;bc8;qd8;ke8;bf8;ng8;rh8;", chessBoard.getPositionString());
  }
}
