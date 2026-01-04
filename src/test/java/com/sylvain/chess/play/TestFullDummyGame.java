package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.play.players.DummyPlayer;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestFullDummyGame {
  @Test
  public void testFullDummyGame() {
    final ChessBoard board = ChessBoard.startingPositions();
    final Gameplay play = new Gameplay(board, List.of(new DummyPlayer(Color.WHITE), new DummyPlayer(Color.BLACK)));
    final GameStatus gameStatus = play.playGame();
    System.out.println(gameStatus + " after " + play.getMoveNumber() + " moves.");
    // OBS: checkmate by moving white's king!
    Assert.assertEquals(GameStatus.CHECKMATE, gameStatus);
    Assert.assertEquals(57, play.getMoveNumber());
    int numberOfUnmovedPawns = 0;
    for (PieceOnBoard piece : board.getPieces(Color.WHITE).values()) {
      // OBS: one single pawn didn't move during the game
      if (piece.getSquare().getRow() != 2 || !(piece instanceof Pawn))
        Assert.assertTrue(piece.isHasAlreadyMoved());
      else {
        numberOfUnmovedPawns++;
        Assert.assertFalse(piece.isHasAlreadyMoved());
      }
    }
    for (PieceOnBoard piece : board.getPieces(Color.BLACK).values()) {
      Assert.assertTrue(piece.isHasAlreadyMoved());
    }
    Assert.assertEquals(1, numberOfUnmovedPawns);
  }

  @Test
  public void testDeterminism() {
    Integer consistentMoveNumber = null;
    GameStatus consistentGameStatus = null;
    String consistentGamePositionString = null;
    for(int i = 0; i < 5; i++) {
      final ChessBoard board = ChessBoard.startingPositions();
      final Gameplay play = new Gameplay(board, List.of(new DummyPlayer(Color.WHITE), new DummyPlayer(Color.BLACK)), null, 5, 2);
      final GameStatus gameStatus = play.playGame();
      System.out.println(gameStatus + " after " + play.getMoveNumber() + " moves.");
      if (consistentGameStatus != null) {
        Assert.assertEquals(consistentMoveNumber.intValue(), play.getMoveNumber());
        Assert.assertEquals(consistentGameStatus, gameStatus);
        Assert.assertEquals(consistentGamePositionString, play.getBoard().getPositionString());
      }
      else {
        consistentMoveNumber = play.getMoveNumber();
        consistentGameStatus = gameStatus;
        consistentGamePositionString = play.getBoard().getPositionString();
      }
    }
  }
}
