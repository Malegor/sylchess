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
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Log
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

  public static ChessBoard startingPositions() {
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
        return isColumnInBoard(square.getColumn()) && isRowInBoard(square.getRow());
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
        if (piece instanceof King) {
            this.kings.put(piece.getColor(), (King) piece);
        }
        if (oldPieceColor != null || oldPiece != null) {
            log.warning("The following piece was already on the board! " + oldPieceColor + " - " + oldPiece);
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

    public List<PieceOnBoard> piecesCheckingKing(final Color color) {
      if (!this.kings.containsKey(color))
        return List.of();
      return this.piecesControllingSquare(this.kings.get(color).getSquare(), getOppositeColor(color));
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
        if (piece instanceof King) {
          //log.severe("The king shouldn't get to be removed! " + piece);
          this.kings.remove(piece.getColor());
        }
    }

    public List<Move> getAllValidMoves(final Color color) {
      final List<Move> validMoves = new ArrayList<>();
      for (PieceOnBoard piece : new ArrayList<>(this.piecesByColor.get(color).values())) {
        if (piece instanceof Pawn) {
            for (int incrementRow = 1; incrementRow <= 2; incrementRow++) {
                for (int incrementCol = -1 ; incrementCol <= 1 ; incrementCol++) {
                    Square newSquare = piece.getSquare().move(incrementCol, incrementRow * getPawnDirection(color));
                    if (isInBoard(newSquare)) {
                        if (newSquare.getRow() != getPromotionRow(color)) {
                            Move possibleMove = new Move(Map.of(piece, piece.at(newSquare)), this);
                            if (possibleMove.isValidMove()) {
                                validMoves.add(possibleMove);
                            }
                        }
                        else {
                            // Promotion
                            Move possibleMove = new Move(Map.of(piece, new Knight(piece.getColor(), newSquare)), this);
                            if (possibleMove.isValidMove()) {
                                validMoves.add(possibleMove);
                                validMoves.add(new Move(Map.of(piece, new Rook(piece.getColor(), newSquare)), this));
                                validMoves.add(new Move(Map.of(piece, new Bishop(piece.getColor(), newSquare)), this));
                                validMoves.add(new Move(Map.of(piece, new Queen(piece.getColor(), newSquare)), this));
                            }
                        }
                    }
                }
            }
        }
        else {
            Set<Square> squares = piece.getControlledSquares(this).stream().
                    filter(square -> !this.hasPieceAt(square) || piece.getColor() != this.getPieceAt(square).getColor()).collect(Collectors.toSet());
            for (Square square : squares) {
              Move move = new Move(Map.of(piece, piece.at(square)), this);
              if (move.isValidMove()) validMoves.add(move);
            }
        }
      }
      // Castling
      final King king = this.kings.get(color);
      // OBS: more checks could be added in case of puzzles (when we don't know if the piece has already moved or not...)
      //  && king.getSquare().getRow() == getFirstRow(color) && king.getSquare().getColumn() > 1 && king.getSquare().getColumn() < CB.BOARD_COLUMNS // (960)
      // or simply king.getSquare().getColumn() == 5 (classical chess)
      if (king != null && !king.isHasAlreadyMoved()) {
        final Set<PieceOnBoard> rooks = this.piecesByColor.get(color).values().stream().filter(piece -> piece instanceof Rook && !piece.isHasAlreadyMoved()).collect(Collectors.toSet());
        for (PieceOnBoard rook : rooks) {
          // If the rook is on a column after the king's, it is a king-side castle, otherwise a queen-side castle.
          final Move castle = this.getCastleMove(color, rook, king);
          if (castle.isValidMove()) validMoves.add(castle);
        }
      }
      return validMoves;
    }

  private Move getCastleMove(Color color, PieceOnBoard rook, King king) {
    boolean isKingSideCastle = rook.getSquare().getColumn() > king.getSquare().getColumn();
    final int newKingsColumn = isKingSideCastle ? 7 : 3; // Logic under these columns? introduce constants?
    final int newRooksColumn = newKingsColumn + (isKingSideCastle ? -1 : 1);
    return new Move(Map.of(king, new King(color, new Square(newKingsColumn, king.getSquare().getRow())), rook, new Rook(color, new Square(newRooksColumn, rook.getSquare().getRow()))), this);
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
}
