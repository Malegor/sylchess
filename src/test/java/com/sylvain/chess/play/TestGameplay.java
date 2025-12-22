package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.Rook;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.Player;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class TestGameplay {
  @Test
  public void testCheckmate() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new Pawn(Color.WHITE, new Square(1,2)));
    board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
    board.addPiece(new Rook(Color.BLACK, new Square(8,1)));
    board.printBoard();
    final DummyPlayer whitePlayer = new DummyPlayer(Color.WHITE);
    final DummyPlayer blackPlayer = new DummyPlayer(Color.BLACK);
    final List<Player> players = List.of(whitePlayer, blackPlayer);
    final Gameplay game = new Gameplay(board, players);
    final GameStatus status = game.playGame();
    Assert.assertNotNull(status);
    Assert.assertEquals(GameStatus.CHECKMATE, status);
    Assert.assertEquals(whitePlayer, game.getLastPlayer());
  }

  @Test
  public void testStalemate() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
    board.addPiece(new Pawn(Color.BLACK, new Square(3,2)));
    board.addPiece(new Pawn(Color.BLACK, new Square(2,3)));
    board.printBoard();
    final DummyPlayer whitePlayer = new DummyPlayer(Color.WHITE);
    final DummyPlayer blackPlayer = new DummyPlayer(Color.BLACK);
    final List<Player> players = List.of(whitePlayer, blackPlayer);
    final Gameplay game = new Gameplay(board, players);
    final GameStatus status = game.playGame();
    Assert.assertNotNull(status);
    Assert.assertEquals(GameStatus.STALEMATE, status);
    Assert.assertEquals(whitePlayer, game.getLastPlayer());
  }
}
