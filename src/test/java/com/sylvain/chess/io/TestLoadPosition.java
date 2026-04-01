package com.sylvain.chess.io;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.GameVariant;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.io.fen.FenSaver;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.TestFullDummyGame;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TestLoadPosition {

  @Test
  public void testLoadStartingPositionsBoard() {
    final String fenBoard = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR";
    final ChessBoard chessBoard = FenLoader.loadBoard(fenBoard);
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Ra1;Nb1;Bc1;Qd1;Ke1;Bf1;Ng1;Rh1;Pa2;Pb2;Pc2;Pd2;Pe2;Pf2;Pg2;Ph2;pa7;pb7;pc7;pd7;pe7;pf7;pg7;ph7;ra8;nb8;bc8;qd8;ke8;bf8;ng8;rh8;", chessBoard.getPositionString());
    Assert.assertEquals(fenBoard, FenSaver.getBoardString(chessBoard));
  }

  @Test
  public void testAfterMovingPawnBoard() {
    final String fenBoard = "rnbqkbnr/pppppppp/8/8/4P3/8/PPPP1PPP/RNBQKBNR";
    final ChessBoard chessBoard = FenLoader.loadBoard(fenBoard);
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Ra1;Nb1;Bc1;Qd1;Ke1;Bf1;Ng1;Rh1;Pa2;Pb2;Pc2;Pd2;Pf2;Pg2;Ph2;Pe4;pa7;pb7;pc7;pd7;pe7;pf7;pg7;ph7;ra8;nb8;bc8;qd8;ke8;bf8;ng8;rh8;", chessBoard.getPositionString());
    Assert.assertEquals(fenBoard, FenSaver.getBoardString(chessBoard));
  }

  @Test
  public void testMateIn3Board() {
    final String fenBoard = "1kr4r/ppp2p2/5bpq/4N3/4PP2/1b4P1/PPP2Q1P/R5K1";
    final ChessBoard chessBoard = FenLoader.loadBoard(fenBoard);
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Ra1;Kg1;Pa2;Pb2;Pc2;Qf2;Ph2;bb3;Pg3;Pe4;Pf4;Ne5;bf6;pg6;qh6;pa7;pb7;pc7;pf7;kb8;rc8;rh8;", chessBoard.getPositionString());
    Assert.assertEquals(fenBoard, FenSaver.getBoardString(chessBoard));
  }

  @Test
  public void testMateIn4Board() {
    final String fenBoard = "4q3/1p3R1p/r3r3/4p1kP/p1Pp2P1/1P1P3K/6P1/5R2";
    final ChessBoard chessBoard = FenLoader.loadBoard(fenBoard);
    chessBoard.printBoard();
    System.out.println(chessBoard.getPositionString());
    Assert.assertEquals("Rf1;Pg2;Pb3;Pd3;Kh3;pa4;Pc4;pd4;Pg4;pe5;kg5;Ph5;ra6;re6;pb7;Rf7;ph7;qe8;", chessBoard.getPositionString());
    Assert.assertEquals(fenBoard, FenSaver.getBoardString(chessBoard));
  }

  @Test
  public void testLoadStartingPositions() throws IOException {
    final String fileName = "fen/starting.fen";
    final Gameplay gameplay = loadPositionFromFile(fileName);
    gameplay.playGame(TestFullDummyGame.getDummyPlayers(gameplay.getBoard()), 0);
    Assert.assertEquals(PlayerColor.BLACK, gameplay.getInfo().getLastPlayer().getColor());
    for (PlayerColor color : Set.of(PlayerColor.WHITE, PlayerColor.BLACK)) {
      Assert.assertFalse(gameplay.getBoard().getKing(color).isHasAlreadyMoved());
      Assert.assertEquals(2, gameplay.getBoard().getUnmovedRooks(color).size());
    }
    Assert.assertNull(gameplay.getBoard().getPreviousMove());
    Assert.assertEquals(1, gameplay.getInfo().getMoveNumber());
    Assert.assertEquals(1, gameplay.getInfo().getLastHalfMoveWithCaptureOrPawn());
    Assert.assertEquals(loadStringFromFile(fileName), FenSaver.getPositionString(gameplay));
  }

  @Test
  public void testAfterMovingPawn() throws IOException {
    final String fileName = "fen/after-pawn.fen";
    final Gameplay gameplay = loadPositionFromFile(fileName);
    gameplay.playGame(TestFullDummyGame.getDummyPlayers(gameplay.getBoard()), 0);
    Assert.assertEquals(PlayerColor.WHITE, gameplay.getInfo().getLastPlayer().getColor());
    for (PlayerColor color : Set.of(PlayerColor.WHITE, PlayerColor.BLACK)) {
      Assert.assertFalse(gameplay.getBoard().getKing(color).isHasAlreadyMoved());
      Assert.assertEquals(2, gameplay.getBoard().getUnmovedRooks(color).size());
    }
    Assert.assertNotNull(gameplay.getBoard().getPreviousMove());
    final Square startingSquare = new Square(4, 4);
    final Pawn blackPawn = new Pawn(PlayerColor.BLACK, startingSquare);
    Assert.assertTrue((new Move(Map.of(blackPawn, blackPawn.move(1, -1)), gameplay.getBoard())).isValidMove());
    // OBS: this pawn didn't exist in the board, it has to be removed (as the rollback method will restore the key's position).
    gameplay.getBoard().removePiece(blackPawn);
    Assert.assertEquals(1, gameplay.getInfo().getMoveNumber());
    Assert.assertEquals(1, gameplay.getInfo().getLastHalfMoveWithCaptureOrPawn());
    Assert.assertEquals(loadStringFromFile(fileName), FenSaver.getPositionString(gameplay));
  }

  @Test
  public void testMateIn3() throws IOException {
    final String fileName = "fen/mate3.fen";
    final Gameplay gameplay = loadPositionFromFile(fileName);
    gameplay.playGame(TestFullDummyGame.getDummyPlayers(gameplay.getBoard()), 0);
    Assert.assertEquals(PlayerColor.BLACK, gameplay.getInfo().getLastPlayer().getColor());
    for (PlayerColor color : Set.of(PlayerColor.WHITE, PlayerColor.BLACK)) {
      Assert.assertFalse(gameplay.getBoard().getKing(color).isHasAlreadyMoved());
      Assert.assertTrue(gameplay.getBoard().getUnmovedRooks(color).isEmpty());
    }
    Assert.assertNull(gameplay.getBoard().getPreviousMove());
    Assert.assertEquals(1, gameplay.getInfo().getMoveNumber());
    Assert.assertEquals(1, gameplay.getInfo().getLastHalfMoveWithCaptureOrPawn());
    Assert.assertEquals(loadStringFromFile(fileName), FenSaver.getPositionString(gameplay));
  }

  @Test
  public void testMateIn4() throws IOException {
    final String fileName = "fen/mate4.fen";
    final Gameplay gameplay = loadPositionFromFile(fileName);
    gameplay.playGame(TestFullDummyGame.getDummyPlayers(gameplay.getBoard()), 0);
    Assert.assertEquals(PlayerColor.BLACK, gameplay.getInfo().getLastPlayer().getColor());
    for (PlayerColor color : Set.of(PlayerColor.WHITE, PlayerColor.BLACK)) {
      Assert.assertFalse(gameplay.getBoard().getKing(color).isHasAlreadyMoved());
      Assert.assertTrue(gameplay.getBoard().getUnmovedRooks(color).isEmpty());
    }
    Assert.assertNull(gameplay.getBoard().getPreviousMove());
    Assert.assertEquals(1, gameplay.getInfo().getMoveNumber());
    Assert.assertEquals(1, gameplay.getInfo().getLastHalfMoveWithCaptureOrPawn());
    Assert.assertEquals(loadStringFromFile(fileName), FenSaver.getPositionString(gameplay));
  }

  @Test
  public void testCastling() {
    // OBS: it is not a "classical chess" position, because at least on castle char is not 'k' or 'q'.
    // It is not a chess 960 game either, as there are over 2 possible castles.
    final String fen = "r1rrkrrr/8/8/8/8/8/8/RRRKRRRR w HGCAhda - 0 1";
    final Gameplay gameplay = FenLoader.loadPosition(fen);
    gameplay.playGame(TestFullDummyGame.getDummyPlayers(gameplay.getBoard()), 0);
    Assert.assertEquals(Set.of('a', 'd', 'h'), gameplay.getBoard().getUnmovedRooks(PlayerColor.BLACK).stream().map(r -> r.getSquare().getColumnLetter()).collect(Collectors.toSet()));
    Assert.assertEquals(Set.of('a', 'c', 'g', 'h'), gameplay.getBoard().getUnmovedRooks(PlayerColor.WHITE).stream().map(r -> r.getSquare().getColumnLetter()).collect(Collectors.toSet()));
    Assert.assertEquals(fen, FenSaver.getPositionString(gameplay));
    Assert.assertEquals(GameVariant.UNKNOWN, gameplay.getBoard().getVariant());
  }

  @Test
  public void testCastlingClassicalChess() {
    final String fen = "r1rrkrrr/8/8/8/8/8/8/RRRKRRRR w kq - 0 1";
    final Gameplay gameplay = FenLoader.loadPosition(fen);
    gameplay.playGame(TestFullDummyGame.getDummyPlayers(gameplay.getBoard()), 0);
    Assert.assertEquals(Set.of('a', 'h'), gameplay.getBoard().getUnmovedRooks(PlayerColor.BLACK).stream().map(r -> r.getSquare().getColumnLetter()).collect(Collectors.toSet()));
    Assert.assertTrue(gameplay.getBoard().getUnmovedRooks(PlayerColor.WHITE).isEmpty());
    Assert.assertEquals(fen, FenSaver.getPositionString(gameplay));
    Assert.assertEquals(GameVariant.CLASSICAL, gameplay.getBoard().getVariant());
  }

  public static Gameplay loadPositionFromFile(final String fileName) throws IOException {
    return FenLoader.loadPosition(loadStringFromFile(fileName));
  }

  private static String loadStringFromFile(final String fileName) throws IOException {
    final ClassLoader classloader = Thread.currentThread().getContextClassLoader();
    final InputStream is = classloader.getResourceAsStream(fileName);
    return new String(is.readAllBytes(), StandardCharsets.UTF_8);
  }
}
