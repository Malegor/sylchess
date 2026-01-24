package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestFullDummyGame {
  @Test
  public void testFullDummyGame() {
    final ChessBoard board = ChessBoard.defaultBoard();
    final Gameplay play = new Gameplay(board, null, 5, 2);
    final GameStatus gameStatus = play.playGame(getDummyPlayers(board));
    System.out.println(gameStatus + " after " + play.getMoveNumber() + " moves.");
    Assert.assertEquals(GameStatus.UNIMPROVING_MOVES, gameStatus);
    Assert.assertEquals(57, play.getMoveNumber());
    int numberOfUnmovedPawns = 0;
    for (PieceOnBoard piece : board.getPieces(Color.WHITE).values()) {
      // OBS: one single pawn didn't move during the game
      if (piece.getSquare().row() != 2 || !piece.getName().equals(Pawn.NAME_LC))
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
      final ChessBoard board = ChessBoard.defaultBoard();
      final Gameplay play = new Gameplay(board, null, 5, 2);
      final GameStatus gameStatus = play.playGame(getDummyPlayers(board));
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

  public static List<Player> getDummyPlayers(final ChessBoard board) {
    return List.of(new DummyPlayer(Color.WHITE, board), new DummyPlayer(Color.BLACK, board));
  }
}
