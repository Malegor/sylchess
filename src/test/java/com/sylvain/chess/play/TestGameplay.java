package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.Rook;
import com.sylvain.chess.play.players.DummyPlayer;
import com.sylvain.chess.play.players.Player;
import com.sylvain.chess.utils.CircularIterator;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;
import java.util.Map;

public class TestGameplay {
  @Test
  public void testCheckmate() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new Pawn(Color.WHITE, new Square(1,2)));
    board.addPiece(new Pawn(Color.WHITE, new Square(2,2)));
    board.addPiece(new Rook(Color.BLACK, new Square(8,1)));
    board.printBoard();
    final DummyPlayer whitePlayer = new DummyPlayer(Color.WHITE, board);
    final DummyPlayer blackPlayer = new DummyPlayer(Color.BLACK, board);
    final List<Player> players = List.of(whitePlayer, blackPlayer);
    final Gameplay game = new Gameplay(board);
    final GameStatus status = game.playGame(players);
    Assert.assertNotNull(status);
    Assert.assertEquals(GameStatus.CHECKMATE, status);
    Assert.assertEquals(EndGame.BLACK_WINS, game.getEndGame());
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
    final DummyPlayer whitePlayer = new DummyPlayer(Color.WHITE, board);
    final DummyPlayer blackPlayer = new DummyPlayer(Color.BLACK, board);
    final List<Player> players = List.of(whitePlayer, blackPlayer);
    final Gameplay game = new Gameplay(board);
    final GameStatus status = game.playGame(players);
    Assert.assertNotNull(status);
    Assert.assertEquals(GameStatus.STALEMATE, status);
    Assert.assertEquals(EndGame.DRAW, game.getEndGame());
    Assert.assertEquals(whitePlayer, game.getLastPlayer());
  }

  @Test
  public void testSamePosition() {
    final Gameplay game = this.getGameWithRepeatedMoves(50);
    final GameStatus status = game.playGame(this.getPlayersRepeatingMoves(game.getBoard()));
    Assert.assertNotNull(status);
    Assert.assertEquals(GameStatus.SEVERAL_TIMES_SAME_POSITION, status);
    Assert.assertEquals(EndGame.DRAW, game.getEndGame());
    Assert.assertEquals(Color.BLACK, game.getLastPlayer().getColor());
    // Repetition every 6 moves, as every 3 moves we get the same position but with inverted colors.
    Assert.assertEquals(13, game.getMoveNumber());
  }

  @Test
  public void testMovesWithoutImprovement() {
    final int numberOfMoves = 10;
    final Gameplay game = this.getGameWithRepeatedMoves(numberOfMoves);
    final GameStatus status = game.playGame(this.getPlayersRepeatingMoves(game.getBoard()));
    Assert.assertNotNull(status);
    Assert.assertEquals(GameStatus.UNIMPROVING_MOVES, status);
    Assert.assertEquals(EndGame.DRAW, game.getEndGame());
    Assert.assertEquals(Color.WHITE, game.getLastPlayer().getColor());
    Assert.assertEquals(numberOfMoves + 1, game.getMoveNumber());
  }

  @Test
  public void testOnlyKings() {
    final ChessBoard board = new ChessBoard();
    board.addPiece(new King(Color.WHITE, new Square(1,1)));
    board.addPiece(new King(Color.BLACK, new Square(8,8)));
    final Gameplay game = new Gameplay(board);
    final GameStatus status = game.playGame(TestFullDummyGame.getDummyPlayers(game.getBoard()));
    Assert.assertEquals(GameStatus.ONLY_KINGS, status);
    Assert.assertEquals(EndGame.DRAW, game.getEndGame());
    Assert.assertEquals(1, game.getMoveNumber());
  }

  private Gameplay getGameWithRepeatedMoves(final int maxNumberOfMovesWithoutCaptureOrPawnMove) {
    final ChessBoard board = new ChessBoard();
    final King whiteKing = new King(Color.WHITE, new Square(5, 1));
    board.addPiece(whiteKing);
    final Rook blackRook = new Rook(Color.BLACK, new Square(1, 8));
    board.addPiece(blackRook);
    board.printBoard();
    return new Gameplay(board, null, maxNumberOfMovesWithoutCaptureOrPawnMove, 3);
  }

  private List<Player> getPlayersRepeatingMoves(final ChessBoard board) {
    final King whiteKing = board.getKing(Color.WHITE);
    final Rook blackRook = board.getUnmovedRooks(Color.BLACK).stream().findFirst().orElse(null);
    final Player whitePlayer = new Player(Color.WHITE, "White", board) {
      private final King square2 = new King(Color.WHITE, new Square(6,1));
      private final List<Move> moves = List.of(
              new Move(Map.of(whiteKing, square2), board),
              new Move(Map.of(square2, whiteKing), board));
      final CircularIterator<Move> it = new CircularIterator<>(moves);
      @Override
      protected Move selectMove(List<Move> validMoves) {
        return it.next();
      }
    };
    final Player blackPlayer = new Player(Color.BLACK, "Black", board) {
      private final Rook square2 = new Rook(Color.BLACK, new Square(1,7));
      private final Rook square3 = new Rook(Color.BLACK, new Square(1,6));
      private final List<Move> moves = List.of(
              new Move(Map.of(blackRook, square2), board),
              new Move(Map.of(square2, square3), board),
              new Move(Map.of(square3, blackRook), board));
      final CircularIterator<Move> it = new CircularIterator<>(moves);
      @Override
      protected Move selectMove(List<Move> validMoves) {
        return it.next();
      }
    };
    return List.of(whitePlayer, blackPlayer);
  }
}
