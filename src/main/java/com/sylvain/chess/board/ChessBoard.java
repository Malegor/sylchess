package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.NoPiece;
import com.sylvain.chess.pieces.*;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
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

    public ChessBoard() {
        this.piecesByColor = Map.of(Color.WHITE, new HashMap<>(16), Color.BLACK, new HashMap<>(16));
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
        return color == Color.WHITE ? 1 : 8;
    }

    public static int getPromotionRow(final Color color) {
        return color == Color.WHITE ? 8 : 1;
    }

    public static int getPawnDirection(final Color color) {
        return color == Color.WHITE ? 1 : -1;
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

    private Color getOppositeColor(final Color color) {
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
      final List<PieceOnBoard> piecesChecking = new ArrayList<>(2);
      for (Map.Entry<Square, PieceOnBoard> squarePiece : this.piecesByColor.get(this.getOppositeColor(color)).entrySet()) {
        if (squarePiece.getValue().getControlledSquares(this).contains(this.kings.get(color).getSquare())) {
          piecesChecking.add(squarePiece.getValue());
        }
      }
      return piecesChecking;
    }

    public void movePiece(final PieceOnBoard origin, final PieceOnBoard destination) {
        this.removePiece(origin);
        final PieceOnBoard captured = this.getPieceAt(destination.getSquare());
        if (captured != null) this.removePiece(captured);
        this.addPiece(destination);
    }

    public void removePiece(final PieceOnBoard piece) {
        this.piecesByColor.get(piece.getColor()).remove(piece.getSquare());
        this.allPieces.remove(piece.getSquare());
        if (piece instanceof King)
            this.kings.remove(piece.getColor());
    }

    public Set<Move> getAllValidMoves(final Color color) {
        final Set<Move> validMoves = new HashSet<>();
        for (PieceOnBoard piece : new HashSet<>(this.piecesByColor.get(color).values())) {
            if (piece instanceof Pawn) {
                for (int incrementRow = 1; incrementRow <= 2; incrementRow++) {
                    for (int incrementCol = -1 ; incrementCol <= 1 ; incrementCol++) {
                        Square newSquare = piece.getSquare().move(incrementCol, incrementRow * getPawnDirection(color));
                        if (isInBoard(newSquare)) {
                            if (newSquare.getRow() != getPromotionRow(color)) {
                                Move possibleMove = new Move(Map.of(piece, new Pawn(piece.getColor(), newSquare)), this);
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
        return validMoves;
    }
}
