package com.sylvain.chess.board;

import com.sylvain.chess.PlayerColor;
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
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Log4j2
public class ChessBoard {
  public static final int BOARD_COLS = 8;
  public static final int BOARD_ROWS = 8;
  public static final int CLASSICAL_KING_COLUMN = 5;
  private static final List<Integer> base_sequence_b_b_q_nn = List.of(4, 4, 6, 10);
  private final Map<PlayerColor, Map<Square, PieceOnBoard>> piecesByColor;
  private final Map<Square, PieceOnBoard> allPieces;
  private final Map<PlayerColor, King> kings;
  @Getter @Setter
  private Move previousMove = null;
  @Getter
  private final GameVariant variant;
  @Getter
  private final boolean setUp;
  @Getter
  private final int index960;

  // For knights: C{5,2} possible positions = 5x4/2 (and not 5x4)
  private record Knights(int first, int second) {}
  private static final List<Knights> KNIGHTS_COMBINATIONS_52 = List.of(
          new Knights(0, 0), new Knights(0, 1), new Knights(0, 2), new Knights(0, 3),
          new Knights(1, 1), new Knights(1, 2), new Knights(1, 3),
          new Knights(2, 2), new Knights(2, 3),
          new Knights(3, 3));

  private ChessBoard(final List<Character> positions, final GameVariant variant, final boolean setUp, final Integer index960) {
    this.piecesByColor = Map.of(PlayerColor.WHITE, new LinkedHashMap<>(16), PlayerColor.BLACK, new LinkedHashMap<>(16));
    this.allPieces = new LinkedHashMap<>(32);
    this.kings = new HashMap<>(2);
    this.putPositionsForColor(PlayerColor.WHITE, positions);
    this.putPositionsForColor(PlayerColor.BLACK, positions);
    this.variant = variant;
    this.setUp = setUp;
    this.index960 = index960 == null ? get960IndexFromPositions(positions) : index960;
  }

  public static ChessBoard defaultBoard() {
    return new ChessBoard(getClassicalPiecesPositions(), GameVariant.CLASSICAL, false, null);
  }

  public static ChessBoard emptyBoard(final GameVariant variant) {
    return new ChessBoard(List.of(), variant, true, -1);
  }

  public static ChessBoard emptyBoard() {
    return emptyBoard(GameVariant.CLASSICAL);
  }

  public static ChessBoard board960BySeed(final Long seed) {
    List<Character> positions = get960PiecesPositions(seed);
    final int index = get960IndexFromPositions(positions);
    return new ChessBoard(positions, GameVariant.CHESS960, false, null);
  }

  public static ChessBoard board960ByIndex(final int index) {
    return new ChessBoard(get960PositionsFromIndex(index), GameVariant.CHESS960, false, index);
  }

  public ChessBoard copy() {
    final ChessBoard copy = new ChessBoard(List.of(), this.getVariant(), this.isSetUp(), this.index960);
    for (PieceOnBoard piece : new ArrayList<>(this.allPieces.values())) {
      copy.addPiece(piece);
    }
    return copy;
  }

  public static int getFirstRow(final PlayerColor color) {
    return getRowForColor(1, color);
  }

  public static int getPromotionRow(final PlayerColor color) {
    return getRowForColor(ChessBoard.BOARD_ROWS, color);
  }

  public static int getPawnDirection(final PlayerColor color) {
      return color == PlayerColor.WHITE ? 1 : -1;
  }

  public static int getRowForColor(int row, PlayerColor color) {
    return color == PlayerColor.WHITE ? row : ChessBoard.BOARD_ROWS - row + 1;
  }

  private void putPositionsForColor(final PlayerColor color, final List<Character> positions) {
    final int firstRow = getFirstRow(color);
    int column = 1;
    for (final char pieceChar : positions) {
      this.addPiece(PieceOnBoard.createPiece(color.changeChar().apply(pieceChar), new Square(column++, firstRow)));
    }
    if (!positions.isEmpty())
      this.addPawnsToSecondRow(color);
  }

  private static List<Character> getClassicalPiecesPositions() {
    return List.of(Rook.NAME_LC, Knight.NAME_LC, Bishop.NAME_LC, Queen.NAME_LC, King.NAME_LC, Bishop.NAME_LC, Knight.NAME_LC, Rook.NAME_LC);
  }

  private static List<Character> get960PiecesPositions(final Long seed) {
    final Random random = getRandom(seed);
    final List<Integer> description960 = base_sequence_b_b_q_nn.stream().map(random::nextInt).toList();
    return get960PiecesPositions(description960);
  }

  private static List<Character> get960PositionsFromIndex(final int index) {
    if (index <= 0 || index > 960) {
      throw new IllegalArgumentException("Invalid index: " + index);
    }
    // Build list of positions from index
    final List<Integer> bases = base_sequence_b_b_q_nn;
    final List<Integer> positions = new ArrayList<>(bases.size());
    int n = index - 1;
    for (int i = bases.size() - 1; i >= 0; i--) {
      positions.addFirst(n % bases.get(i));
      n = n / bases.get(i);
    }
    if (index != getChess960Index(positions))
      throw new IllegalStateException("Invalid Chess-960 position for: " + index + " != " + getChess960Index(positions));
    return get960PiecesPositions(positions);
  }

  /**
   * @param positions960 First the position of both bishops (each one on a different square color), then the position of the queen (considering only free
   *                     columns), then the knights. The remaining positions are occupied, in this order, by rook, king and rook.
   * @return The list of pieces, column by column, for the informed sequence of positions.
   */
  private static List<Character> get960PiecesPositions(final List<Integer> positions960) {
    int i = 0;
    final int bishop1Position = positions960.get(i++) * 2;
    final int bishop2Position = positions960.get(i++) * 2 + 1;
    final List<Integer> alreadyGivenPositions = new ArrayList<>(List.of(bishop1Position, bishop2Position));
    alreadyGivenPositions.sort(Integer::compareTo);
    final int queenPosition = getPosition(positions960.get(i++), alreadyGivenPositions);
    final Knights knights = KNIGHTS_COMBINATIONS_52.get(positions960.get(i));
    final int knight1Position = getPosition(knights.first(), alreadyGivenPositions);
    final int knight2Position = getPosition(knights.second(), alreadyGivenPositions);
    final List<Integer> freePositions = new ArrayList<>(IntStream.rangeClosed(0, 7).boxed().toList());
    freePositions.removeAll(alreadyGivenPositions);
    final char[] positions = new char[8];
    positions[bishop1Position] = Bishop.NAME_LC;
    positions[bishop2Position] = Bishop.NAME_LC;
    positions[queenPosition] = Queen.NAME_LC;
    positions[knight1Position] = Knight.NAME_LC;
    positions[knight2Position] = Knight.NAME_LC;
    positions[freePositions.get(0)] = Rook.NAME_LC;
    positions[freePositions.get(1)] = King.NAME_LC;
    positions[freePositions.get(2)] = Rook.NAME_LC;
    return new String(positions).chars().mapToObj(c -> (char) c).collect(Collectors.toList());
  }

  private static int getPosition(final int relativePosition, final List<Integer> alreadyGivenPositions) {
    int finalPosition = relativePosition;
    for (int index = 0; index < alreadyGivenPositions.size(); index++) {
      int position = alreadyGivenPositions.get(index);
      if (position <= finalPosition) {
        finalPosition++;
      }
      else {
        alreadyGivenPositions.add(index, finalPosition);
        return finalPosition;
      }
    }
    alreadyGivenPositions.add(finalPosition);
    return finalPosition;
  }

  private static int get960IndexFromPositions(final List<Character> positions) {
    if (positions.size() < 8)
      return -1;
    final List<Character> positionsCopy = new ArrayList<>(positions);
    final List<Integer> positionsBBQN2 = new ArrayList<>(4);
    final Map<Character, List<Integer>> positionsOfPieces = new HashMap<>(5);
    for (int index = 0; index < positionsCopy.size(); index++) {
      final char piece = positionsCopy.get(index);
      positionsOfPieces.putIfAbsent(piece, new ArrayList<>(2));
      positionsOfPieces.get(piece).add(index);
    }
    // TODO: validations + exceptions
    int oddBishop = -1;
    int evenBishop = -1;
    for (int i = 0; i < positionsOfPieces.get('b').size(); i++) {
      int position = positionsOfPieces.get('b').get(i);
      if (position % 2 == 0)
        oddBishop = position;
      else
        evenBishop = position;
    }
    positionsBBQN2.add(oddBishop / 2);
    positionsBBQN2.add(evenBishop / 2);
    positionsCopy.remove(Math.max(oddBishop, evenBishop));
    positionsCopy.remove(Math.min(oddBishop, evenBishop));
    final int queenPosition = positionsCopy.indexOf('q');
    positionsBBQN2.add(queenPosition);
    positionsCopy.remove(queenPosition);
    final int knight1position = positionsCopy.indexOf('n');
    positionsCopy.remove(knight1position);
    final int knight2position = positionsCopy.indexOf('n');
    positionsBBQN2.add(KNIGHTS_COMBINATIONS_52.indexOf(new Knights(knight1position, knight2position)));
    return getChess960Index(positionsBBQN2);
  }

  private static Random getRandom(final Long seed) {
    final String logMessage = "Random Seed: {}";
    if (seed != null) {
      log.info(logMessage, seed);
      return new Random(seed);
    }
    final Random random = new Random();
    final long specificSeed = random.nextLong();
    random.setSeed(specificSeed);
    log.info(logMessage, specificSeed);
    return random;
  }

  private void addPawnsToSecondRow(final PlayerColor color) {
    final int secondRow = getFirstRow(color) + getPawnDirection(color);
    for (int col = 1 ; col <= ChessBoard.BOARD_COLS ; col++) {
        this.addPiece(new Pawn(color, new Square(col, secondRow)));
    }
  }

  private static int getChess960Index(final List<Integer> piecesPositions) {
    int number = 0;
    for (int index = 0; index < piecesPositions.size(); index++) {
      number += piecesPositions.get(index);
      if (index <  piecesPositions.size() - 1)
        number *= base_sequence_b_b_q_nn.get(index + 1);
    }
    return number + 1;
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

  public static PlayerColor getOppositeColor(final PlayerColor color) {
    return color == PlayerColor.WHITE ? PlayerColor.BLACK : PlayerColor.WHITE;
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
        PieceOnBoard piece = this.piecesByColor.get(PlayerColor.WHITE).get(new Square(j, i));
        if (piece == null) {
          piece = this.piecesByColor.get(PlayerColor.BLACK).get(new Square(j, i));
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

  public List<PieceOnBoard> findPiecesCheckingKing(final PlayerColor color) {
    return !this.kings.containsKey(color) ? List.of() : this.piecesControllingSquare(this.kings.get(color).getSquare(), getOppositeColor(color));
  }

  public List<PieceOnBoard> piecesControllingSquare(final Square square, final PlayerColor color) {
    final List<PieceOnBoard> piecesControlling = new ArrayList<>(2);
    for (Map.Entry<Square, PieceOnBoard> squarePiece : this.piecesByColor.get(color).entrySet()) {
      if (squarePiece.getValue().getControlledSquares(this).contains(square)) {
        piecesControlling.add(squarePiece.getValue());
      }
    }
    return piecesControlling;
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

  public List<Move> findAllValidMoves(final PlayerColor color) {
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
      final List<Rook> rooks = this.getUnmovedRooks(color);
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
  public List<Rook> getUnmovedRooks(final PlayerColor color) {
    return this.piecesByColor.get(color).values().stream().filter(piece -> piece.getName().equals(Rook.NAME_LC) && !piece.isHasAlreadyMoved())
            .map(piece -> (Rook) piece).collect(Collectors.toList());
  }

  public Move getCastleMove(final King king, final Rook rook) {
    final boolean isKingSideCastle = areValidSquaresForCastle(king, rook, true);
    final int newKingsColumn = isKingSideCastle ? CLASSICAL_KING_COLUMN + 2 : CLASSICAL_KING_COLUMN - 2;
    final int newRooksColumn = newKingsColumn + (isKingSideCastle ? -1 : 1);
    final Map<PieceOnBoard, PieceOnBoard> kingAndRook = new LinkedHashMap<>(2);
    kingAndRook.put(king, king.at(new Square(newKingsColumn, king.getSquare().row())));
    kingAndRook.put(rook, rook.at(new Square(newRooksColumn, rook.getSquare().row())));
    return new Move(kingAndRook, this);
  }

  public static boolean areValidSquaresForCastle(final King king, final Rook rook, final boolean isKingSideCastle) {
    final int kingSideMultiplier = isKingSideCastle? -1 : 1;
    final Square kingSquare = king.getSquare();
    final Square rookSquare = rook.getSquare();
    final PlayerColor color = king.getColor();
    return rook.getColor() == color && kingSquare.row() == rookSquare.row() && kingSquare.row() == getFirstRow(color)
            && kingSideMultiplier * (kingSquare.column() - rookSquare.column()) > 0;
  }

  public Map<Square, PieceOnBoard> getPieces(final PlayerColor color) {
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
    for (Map.Entry<PlayerColor, Map<Square, PieceOnBoard>> square : this.piecesByColor.entrySet()) {
      piecesByColor += square.getValue().size();
    }
    if (piecesByColor != allPieces.size())
      throw new IllegalStateException("Inconsistent number of pieces!");
    for (Map.Entry<Square, PieceOnBoard> square : allPieces.entrySet()) {
      if (!square.getKey().equals(square.getValue().getSquare()))
        throw new IllegalStateException("Inconsistent square for all pieces!");
      final PlayerColor color = square.getValue().getColor();
      final PieceOnBoard piece = this.piecesByColor.get(color).get(square.getKey());
      if (piece == null || !piece.equals(square.getValue()))
        throw new IllegalStateException("Inconsistent piece between both data structures!");
    }
  }

  public King getKing(final PlayerColor color) {
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

  public List<PlayerColor> getColors() {
    return this.piecesByColor.keySet().stream().sorted().collect(Collectors.toList());
  }

  public boolean isKingUnderCheck(final PlayerColor color) {
    return !this.findPiecesCheckingKing(color).isEmpty();
  }

  public boolean isKingCheckMate(final PlayerColor color) {
    return this.isKingUnderCheck(color) && this.findAllValidMoves(color).isEmpty();
  }
}
