package com.sylvain.chess.play;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenSaver;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.play.players.AlphaBetaPlayer;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestFullDummyGame {
  @Test
  public void testFullDummyGame() {
    final ChessBoard board = ChessBoard.defaultBoard();
    final Gameplay play = new Gameplay(board, null, new DrawConditions(5, 2));
    final GameStatus gameStatus = play.playGame(getDummyPlayers(board));
    System.out.println(gameStatus + " after " + play.getInfo().getMoveNumber() + " moves.");
    Assert.assertEquals(GameStatus.UNIMPROVING_MOVES, gameStatus);
    Assert.assertEquals(57, play.getInfo().getMoveNumber());
    int numberOfUnmovedPawns = 0;
    for (PieceOnBoard piece : board.getPieces(PlayerColor.WHITE).values()) {
      // OBS: one single pawn didn't move during the game
      if (piece.getSquare().row() != 2 || !piece.getName().equals(Pawn.NAME_LC))
        Assert.assertTrue(piece.isHasAlreadyMoved());
      else {
        numberOfUnmovedPawns++;
        Assert.assertFalse(piece.isHasAlreadyMoved());
      }
    }
    for (PieceOnBoard piece : board.getPieces(PlayerColor.BLACK).values()) {
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
      final Gameplay play = new Gameplay(board, null, new DrawConditions(5, 2));
      final GameStatus gameStatus = play.playGame(getDummyPlayers(board));
      System.out.println(gameStatus + " after " + play.getInfo().getMoveNumber() + " moves.");
      if (consistentGameStatus != null) {
        Assert.assertEquals(consistentMoveNumber.intValue(), play.getInfo().getMoveNumber());
        Assert.assertEquals(consistentGameStatus, gameStatus);
        Assert.assertEquals(consistentGamePositionString, play.getBoard().getPositionString());
      }
      else {
        consistentMoveNumber = play.getInfo().getMoveNumber();
        consistentGameStatus = gameStatus;
        consistentGamePositionString = play.getBoard().getPositionString();
      }
    }
  }

  @Test
  public void testSpecificGame() {
    String commonFinalFen = null;
    final long seed = 8289664214450011964L;
    int j=0;
    //for(int j = 0; j < 20; j++) {
      final ChessBoard board = ChessBoard.board960BySeed(seed);
      final Gameplay play = new Gameplay(board);
      final GameStatus gameStatus = play.playGame(
              List.of(new DummyPlayer(PlayerColor.WHITE, board), new AlphaBetaPlayer(PlayerColor.BLACK, play, 3)));
      System.out.println(gameStatus + " after " + play.getInfo().getMoveNumber() + " moves.");
      final String fen = FenSaver.getPositionString(play.getInfo(), board);
      if (commonFinalFen == null) {
        commonFinalFen = fen;
      }
      else if (!commonFinalFen.equals(fen)) {
        throw new IllegalStateException("Indeterminism detected after " + (j+1) + " games for seed=" + seed);
      }
    //}
  }

  public static List<Player> getDummyPlayers(final ChessBoard board) {
    return List.of(new DummyPlayer(PlayerColor.WHITE, board), new DummyPlayer(PlayerColor.BLACK, board));
  }
}
