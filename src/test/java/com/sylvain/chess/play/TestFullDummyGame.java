package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.play.players.DummyPlayer;
import org.junit.Test;

import java.util.List;

public class TestFullDummyGame {
  @Test
  public void testFullDummyGame(){
    final ChessBoard board = ChessBoard.startingPositions();
    final Gameplay play = new Gameplay(board, List.of(new DummyPlayer(Color.WHITE), new DummyPlayer(Color.BLACK)), 5, 2);
    final GameStatus gameStatus = play.playGame();
    System.out.println(gameStatus + " after " + play.getMoveNumber() + " moves.");
  }
}
