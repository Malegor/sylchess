package com.sylvain.chess.io;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.pieces.Rook;
import com.sylvain.chess.play.Gameplay;
import com.sylvain.chess.play.players.DummyPlayer;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class FenDecoder {

  public static Gameplay loadPosition(final String fen) {
    final String[] fenArray = fen.split(" ");
    if (fenArray.length < 6)
      throw new IllegalArgumentException("Invalid fen (missing arguments): " + fen);
    final ChessBoard board = loadBoard(fenArray[0]);
    final Color color = getNextColor(fenArray[1]);
    configureImpossibleCastles(fenArray[2], board);
    configureLastMove(fenArray[3], board, ChessBoard.getOppositeColor(color));
    final int numberOfMovesWithoutImprovement = Integer.parseInt(fenArray[4]);
    final int moveNumber = Integer.parseInt(fenArray[5]);
    // TODO: read other fields
    return new Gameplay(board, List.of(new DummyPlayer(Color.WHITE), new DummyPlayer(Color.BLACK)), color); // TODO: players?
  }

  private static void configureLastMove(final String fenEnPassant, final ChessBoard board, final Color color) {
    if (fenEnPassant.equals("-"))
      return;
    final Square enPassantSquare = board.getSquare(fenEnPassant);
    final Pawn pawn = (Pawn) board.getPieceAt(enPassantSquare.move(0, ChessBoard.getPawnDirection(color)));
    board.setPreviousMove(new Move(Map.of(pawn.at(pawn.getSquare().move(0, - 2 * ChessBoard.getPawnDirection(color))), pawn), board));
  }

  private static Color getNextColor(final String fenColor) {
    // OBS: here we permit the configuration of any other string for blacks
    if (!Set.of("w", "b").contains(fenColor)) {
      log.warn("Color is not 'w' or 'b'; it will be considered as WHITE: {}", fenColor);
    }
    return Objects.equals(fenColor, "b") ? Color.BLACK : Color.WHITE;
  }

  private static void configureImpossibleCastles(final String fenCastles, final ChessBoard board) {
    final Set<Character> allPossibleCastles = fenCastles.chars().mapToObj(c -> (char) c).collect(Collectors.toSet());
    final Set<Character> castleChars = Set.of('K', 'Q', 'k', 'q');
    for (final char castle : castleChars) {
      if (!allPossibleCastles.contains(castle)) {
        for (final Rook rook : findRookForCastle(castle, board)) {
          rook.setHasAlreadyMoved(true);
        }
      }
    }
  }

  private static Set<Rook> findRookForCastle(final char castleChar, final ChessBoard board) {
    final Color color = Character.isUpperCase(castleChar) ? Color.WHITE : Color.BLACK;
    final Set<Rook> rooks = board.getUnmovedRooks(color);
    final King king = board.getKing(color);
    final boolean isKingSide = Character.toLowerCase(castleChar) == 'k';
    return king.getSquare().row() != ChessBoard.getFirstRow(king.getColor()) ? rooks :
            rooks.stream().filter(rook -> rook.getSquare().row() != ChessBoard.getFirstRow(rook.getColor()) || ChessBoard.areValidForCastle(king, rook, isKingSide)).collect(Collectors.toSet());
  }

  public static ChessBoard loadBoard(final String fenBoard) {
    final ChessBoard board = new ChessBoard();
    final String[] fenByRow = fenBoard.split("/");
    if (fenByRow.length != ChessBoard.BOARD_ROWS)
      throw new IllegalArgumentException("Invalid fen board (invalid rows): " + fenBoard);
    for (int row = 0; row < fenByRow.length; row++) {
      final String currentRow = fenByRow[row];
      int col = 0;
      for (char character : currentRow.toCharArray()) {
        col++;
        if (Character.isDigit(character)) {
          final int digit = Character.getNumericValue(character);
          if (digit == 0)
            throw new IllegalArgumentException("Invalid fen board character (invalid digits): " + currentRow);
          col += digit - 1;
        }
        else if (Character.isLetter(character)) {
          board.addPiece(PieceOnBoard.createPiece(character, new Square(col, 8 - row)));
        }
        else
          throw new IllegalArgumentException("Invalid fen board character: " + currentRow);
      }
    }
    return board;
  }
}
