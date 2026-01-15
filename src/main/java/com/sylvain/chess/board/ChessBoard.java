package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.Bishop;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Knight;
import com.sylvain.chess.pieces.NoPiece;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log4j2
public class ChessBoard {
  public static final int BOARD_COLS = 8;
  public static final int BOARD_ROWS = 8;
  private final Map<Color, Map<Square, PieceOnBoard>> piecesByColor;
  private final Map<Square, PieceOnBoard> allPieces;
  private final Map<Color, King> kings;
  @Getter @Setter
  private Move previousMove = null;

  public ChessBoard() {
      this.piecesByColor = Map.of(Color.WHITE, new LinkedHashMap<>(16), Color.BLACK, new LinkedHashMap<>(16));
      this.allPieces = new HashMap<>(32);
      this.kings = new HashMap<>(2);
  }

  public static ChessBoard defaultBoard() {
      final ChessBoard board = new ChessBoard();
      board.putClassicalPositionsForMainPieces(Color.WHITE);
      board.putClassicalPositionsForMainPieces(Color.BLACK);
      return board;
  }

  public static int getFirstRow(final Color color) {
      return getRowForColor(1, color);
  }

  public static int getPromotionRow(final Color color) {
      return getRowForColor(ChessBoard.BOARD_ROWS, color);
  }

  public static int getPawnDirection(final Color color) {
        return color == Color.WHITE ? 1 : -1;
    }

  public static int getRowForColor(int row, Color color) {
      return color == Color.WHITE ? row : ChessBoard.BOARD_ROWS - row + 1;
  }

  private void putClassicalPositionsForMainPieces(final Color color) {
      final int firstRow = getFirstRow(color);
      this.addPiece(new Rook(color, new Square(1, firstRow)));
      this.addPiece(new Knight(color, new Square(2, firstRow)));
      this.addPiece(new Bishop(color, new Square(3, firstRow)));
      this.addPiece(new Queen(color, new Square(4, firstRow)));
      this.addPiece(new King(color, new Square(5, firstRow)));
      this.addPiece(new Bishop(color, new Square(6, firstRow)));
      this.addPiece(new Knight(color, new Square(7, firstRow)));
      this.addPiece(new Rook(color, new Square(8, firstRow)));
      final int secondRow = firstRow + getPawnDirection(color);
      for (int col = 1 ; col <= ChessBoard.BOARD_COLS ; col++) {
          this.addPiece(new Pawn(color, new Square(col, secondRow)));
      }
  }

  public static boolean isInBoard(final Square square) {
      return isColumnInBoard(square.column()) && isRowInBoard(square.row());
  }

  public static boolean isColumnInBoard(final int i) {
      return i >= 1 && i <= BOARD_COLS;
  }

  public static boolean isRowInBoard(final int j) {
      return j >= 1 && j <= BOARD_ROWS;
  }

  public static Color getOppositeColor(final Color color) {
      return color == Color.WHITE ? Color.BLACK : Color.WHITE;
  }

  public void addPiece(final PieceOnBoard piece) {
      final PieceOnBoard oldPieceColor = this.piecesByColor.get(piece.getColor()).put(piece.getSquare(), piece);
      final PieceOnBoard oldPiece = this.allPieces.put(piece.getSquare(), piece);
      if (piece.getName().equals(King.NAME_LC)) {
          this.kings.put(piece.getColor(), (King) piece);
      }
      if (oldPieceColor != null || oldPiece != null) {
        log.warn("The following piece was already on the board! {} - {}", oldPieceColor, oldPiece);
      }
  }

  public void printBoard() {
      final List<List<PieceOnBoard>> piecesAtEachRow = this.getPiecesAtEachRow();
      System.out.println(" |a|b|c|d|e|f|g|h|");
      System.out.println(" |---------------|");
      int rowIndex = BOARD_ROWS;
      for (List<PieceOnBoard> piecesAtRow : piecesAtEachRow.reversed()) {
          System.out.print(rowIndex);
          char sep = '|';
          for (PieceOnBoard piece : piecesAtRow) {
              System.out.print(sep + String.valueOf(piece.printOnBoard()));
              sep = ' ';
          }
          rowIndex--;
          System.out.println("|");
      }
      System.out.println(" |---------------|");
      System.out.println(" |a|b|c|d|e|f|g|h|");
  }

  private List<List<PieceOnBoard>> getPiecesAtEachRow() {
      final List<List<PieceOnBoard>> piecesAtEachRow = new ArrayList<>(BOARD_ROWS);
      for (int i = 1; i <= BOARD_ROWS; i++) {
          final List<PieceOnBoard> piecesAtRow = new ArrayList<>(BOARD_COLS);
          for (int j = 1; j <= BOARD_COLS; j++) {
              PieceOnBoard piece = this.piecesByColor.get(Color.WHITE).get(new Square(j, i));
              if (piece == null) {
                  piece = this.piecesByColor.get(Color.BLACK).get(new Square(j, i));
              }
              if (piece == null) {
                  // No piece at the given square
                  piece = new NoPiece(null, null);
              }
              piecesAtRow.add(piece);
          }
          piecesAtEachRow.add(piecesAtRow);
      }
      return piecesAtEachRow;
  }

  public boolean hasPieceAt(final Square square) {
      return this.getPieceAt(square) != null;
  }

  public PieceOnBoard getPieceAt(final Square square) {
      return this.allPieces.get(square);
  }

  public boolean checksOppositeKing(final PieceOnBoard piece) {
    return piece.getControlledSquares(this).contains(this.getKing(ChessBoard.getOppositeColor(piece.getColor())).getSquare());
  }

  public List<PieceOnBoard> piecesCheckingKing(final Color color) {
    return !this.kings.containsKey(color) ? List.of() : this.piecesControllingSquare(this.kings.get(color).getSquare(), getOppositeColor(color));
  }

  public List<PieceOnBoard> piecesControllingSquare(final Square square, final Color color) {
    final List<PieceOnBoard> piecesControlling = new ArrayList<>(2);
    for (Map.Entry<Square, PieceOnBoard> squarePiece : this.piecesByColor.get(color).entrySet()) {
      if (squarePiece.getValue().getControlledSquares(this).contains(square)) {
        piecesControlling.add(squarePiece.getValue());
      }
    }
    return piecesControlling;
  }

  public void simulatePieceMove(final PieceOnBoard origin, final PieceOnBoard destination) {
    //if (origin.getClass() == destination.getClass()) {} // TODO: only change its square attribute? (TODO: rollback move)
    this.removePiece(origin);
    this.addPiece(destination);
  }

  public void removePiece(final PieceOnBoard piece) {
      this.piecesByColor.get(piece.getColor()).remove(piece.getSquare());
      this.allPieces.remove(piece.getSquare());
      // The following should never happen, provided we keep the king instance the same along the game.
      if (piece.getName().equals(King.NAME_LC)) {
        //log.severe("The king shouldn't get to be removed! " + piece);
        this.kings.remove(piece.getColor());
      }
  }

  public List<Move> findAllValidMoves(final Color color) {
    final List<Move> validMoves = new ArrayList<>();
    for (PieceOnBoard piece : new ArrayList<>(this.piecesByColor.get(color).values())) {
      validMoves.addAll(piece.findValidMoves(this));
    }
    // Castling
    final King king = this.kings.get(color);
    // OBS: more checks could be added in case of puzzles (when we don't know if the piece has already moved or not...)
    //  && king.getSquare().getRow() == getFirstRow(color) && king.getSquare().getColumn() > 1 && king.getSquare().getColumn() < CB.BOARD_COLUMNS // (960)
    // or simply king.getSquare().getColumn() == 5 (standard chess)
    if (king != null && !king.isHasAlreadyMoved()) {
      final Set<Rook> rooks = this.getUnmovedRooks(color);
      for (Rook rook : rooks) {
        // If the rook is on a column after the king's, it is a king-side castle, otherwise a queen-side castle.
        final Move castle = this.getCastleMove(king, rook);
        if (castle.isValidMove()) validMoves.add(castle);
      }
    }
    return validMoves;
  }

  /**
   * @param color - The color of the rooks to find.
   * @return A set containing the rooks that didn't move yet.
   */
  public Set<Rook> getUnmovedRooks(final Color color) {
    return this.piecesByColor.get(color).values().stream().filter(piece -> piece.getName().equals(Rook.NAME_LC) && !piece.isHasAlreadyMoved())
            .map(piece -> (Rook) piece).collect(Collectors.toSet());
  }

  public Move getCastleMove(final King king, final Rook rook) {
    final boolean isKingSideCastle = areValidSquaresForCastle(king, rook, true);
    final int newKingsColumn = isKingSideCastle ? 7 : 3; // Logic under these columns? introduce constants?
    final int newRooksColumn = newKingsColumn + (isKingSideCastle ? -1 : 1);
    return new Move(Map.of(king, king.at(new Square(newKingsColumn, king.getSquare().row())), rook, rook.at(new Square(newRooksColumn, rook.getSquare().row()))), this);
  }

  public static boolean areValidSquaresForCastle(final King king, final Rook rook, final boolean isKingSideCastle) {
    final int kingSideMultiplier = isKingSideCastle? -1 : 1;
    final Square kingSquare = king.getSquare();
    final Square rookSquare = rook.getSquare();
    final Color color = king.getColor();
    return rook.getColor() == color && kingSquare.row() == rookSquare.row() && kingSquare.row() == getFirstRow(color)
            && kingSideMultiplier * (kingSquare.column() - rookSquare.column()) > 0;
  }

  public Map<Square, PieceOnBoard> getPieces(final Color color) {
      return this.piecesByColor.get(color);
  }

  public String getPositionString() {
    StringBuilder positions = new StringBuilder();
    for (Square square : this.allPieces.keySet().stream().sorted().toList()) {
      positions.append(this.allPieces.get(square).toString()).append(";");
    }
    return positions.toString();
  }

  public void validateInternalDataStructures() {
    int piecesByColor = 0;
    for (Map.Entry<Color, Map<Square, PieceOnBoard>> square : this.piecesByColor.entrySet()) {
      piecesByColor += square.getValue().size();
    }
    if (piecesByColor != allPieces.size())
      throw new IllegalStateException("Inconsistent number of pieces!");
    for (Map.Entry<Square, PieceOnBoard> square : allPieces.entrySet()) {
      if (!square.getKey().equals(square.getValue().getSquare()))
        throw new IllegalStateException("Inconsistent square for all pieces!");
      final Color color = square.getValue().getColor();
      final PieceOnBoard piece = this.piecesByColor.get(color).get(square.getKey());
      if (piece == null || !piece.equals(square.getValue()))
        throw new IllegalStateException("Inconsistent piece between both data structures!");
    }
  }

  public King getKing(final Color color) {
    return this.kings.get(color);
  }

  public Square getSquare(final String squareName) {
    final char firstColumn = 'a';
    final char firstRow = '1';
    if (squareName.length() != 2 || squareName.charAt(0) < firstColumn || squareName.charAt(0) > (char) (firstColumn + ChessBoard.BOARD_COLS - 1)
        || squareName.charAt(1) < firstRow || squareName.charAt(1) > (char) (firstRow + ChessBoard.BOARD_ROWS - 1))
      throw new IllegalArgumentException("Invalid square name: " + squareName);
    return new Square(squareName.charAt(0) - firstColumn + 1, squareName.charAt(1) - '0');
  }

  public List<Color> getColors() {
    return this.piecesByColor.keySet().stream().sorted().collect(Collectors.toList());
  }

  public boolean isKingUnderCheck(final Color color) {
    return !piecesCheckingKing(color).isEmpty();
  }

  public boolean isKingCheckMate(final Color color) {
    return this.isKingUnderCheck(color) && this.findAllValidMoves(color).isEmpty();
  }
}
