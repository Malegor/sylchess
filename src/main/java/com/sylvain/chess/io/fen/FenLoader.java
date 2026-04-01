package com.sylvain.chess.io.fen;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.GameVariant;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import com.sylvain.chess.play.Gameplay;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class FenLoader {
  public static final String SEP = " ";
  public static final String ROW_SEP = "/";
  public static final String NONE = "-";

  public static Gameplay loadPosition(final String fen) {
    final String[] fenArray = fen.split(SEP);
    if (fenArray.length < 6)
      throw new IllegalArgumentException("Invalid fen (missing " + (6 - fenArray.length) + " argument(s)): " + fen);
    final ChessBoard board = loadBoard(fenArray[0]);
    final PlayerColor color = getNextColor(fenArray[1].toCharArray()[0]);
    board.setVariant(findVariant(fenArray[2]));
    configureImpossibleCastles(fenArray[2], board);
    configureLastMove(fenArray[3], board, ChessBoard.getOppositeColor(color));
    final int numberOfHalfMovesWithoutImprovement = Integer.parseInt(fenArray[4]);
    final int moveNumber = getMoveNumber(fen);
    final Gameplay gameplay = new Gameplay(board, color);
    gameplay.getInfo().setMoveNumber(moveNumber);
    gameplay.getInfo().setLastHalfMoveWithCaptureOrPawn(2 * (moveNumber-1) - numberOfHalfMovesWithoutImprovement + 1);
    return gameplay;
  }

  public static int getMoveNumber(final String fen) {
    return Integer.parseInt(fen.split(SEP)[5]);
  }

  private static void configureLastMove(final String fenEnPassant, final ChessBoard board, final PlayerColor color) {
    if (fenEnPassant.equals(NONE))
      return;
    final Square enPassantSquare = board.getSquare(fenEnPassant);
    final Pawn pawn = (Pawn) board.getPieceAt(enPassantSquare.move(0, ChessBoard.getPawnDirection(color)));
    board.setPreviousMove(new Move(Map.of(pawn.move(0, - 2 * ChessBoard.getPawnDirection(color)), pawn), board));
  }

  private static PlayerColor getNextColor(final Character fenColor) {
    // OBS: here we permit the configuration of any other string for blacks
    final Character whiteFen = PlayerColor.WHITE.getFenName();
    final Character blackFen = PlayerColor.BLACK.getFenName();
    if (!Set.of(whiteFen, blackFen).contains(fenColor)) {
      log.warn("Color '{}' is not '{}' or '{}'; it will be considered as WHITE.", fenColor, whiteFen, blackFen);
    }
    return Objects.equals(fenColor, 'b') ? PlayerColor.BLACK : PlayerColor.WHITE;
  }

  private static GameVariant findVariant(final String possibleCastles) {
    // OBS: consider as well the number of pieces of each kind on the board etc.?
    if (possibleCastles.equals("-"))
      return GameVariant.CLASSICAL;
    if (possibleCastles.length() > 4)
      return GameVariant.UNKNOWN;
    GameVariant variant = GameVariant.CLASSICAL;
    final char[] charArray = possibleCastles.toCharArray();
    for (final PlayerColor color : PlayerColor.values()) {
      if (possibleCastles.chars().mapToObj(c -> (char) c).filter(c -> PieceOnBoard.getColor(c).equals(color)).toList().size() > 2)
        return GameVariant.UNKNOWN;
    }
    for (final Character castle : charArray) {
      if (Character.toLowerCase(castle) <= Square.getColumnLetter(ChessBoard.BOARD_COLS)) {
        variant = GameVariant.CHESS960;
      }
    }
    return variant;
  }

  private static void configureImpossibleCastles(final String fenCastles, final ChessBoard board) {
    final Map<Character, Character> changeCharsToColumns = new HashMap<>(2);
    for (final PlayerColor color : board.getColors()) {
      changeCharsToColumns.put(color.changeChar().apply(King.NAME_LC), color.changeChar().apply(Square.getColumnLetter(ChessBoard.BOARD_COLS)));
      changeCharsToColumns.put(color.changeChar().apply(Queen.NAME_LC), color.changeChar().apply(Square.getColumnLetter(1)));
    }
    final Set<Character> allPossibleCastles = fenCastles.chars().mapToObj(c -> (char) c)
            .map(c -> changeCharsToColumns.getOrDefault(c, c)).collect(Collectors.toSet());
    for (final PlayerColor color : board.getColors())
      for (final Rook rook : board.getUnmovedRooks(color)) {
        final char columnLetter = color.changeChar().apply(rook.getSquare().getColumnLetter());
        if (!allPossibleCastles.contains(columnLetter)) {
          rook.setHasAlreadyMoved(true);
        }
      }
  }

  public static ChessBoard loadBoard(final String fenBoard) {
    final ChessBoard board = new ChessBoard();
    final String[] fenByRow = fenBoard.split(ROW_SEP);
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
    board.setSetUp(true);
    return board;
  }
}
