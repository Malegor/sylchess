package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.Constants;
import com.sylvain.chess.pieces.Piece;
import com.sylvain.chess.pieces.PieceKind;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
public class ChessBoard {
    private final List<Map<Square, Piece>> pieces;

    public ChessBoard() {
        this.pieces = List.of(new HashMap<>(16), new HashMap<>(16));
    }

    public static ChessBoard startingPositions() {
        final ChessBoard board = new ChessBoard();
        final Map<Square, PieceKind> whitePieces = getClassicalPositionsForMainPieces(true);
        for (int i = 1; i <= Constants.BOARD_COLS; i++) {
            whitePieces.put(new Square(i, 2), PieceKind.PAWN);
        }
        final Map<Square, PieceKind> blackPieces = getClassicalPositionsForMainPieces(false);
        for (int i = 1; i <= Constants.BOARD_COLS; i++) {
            blackPieces.put(new Square(i, 7), PieceKind.PAWN);
        }
        board.addPieces(Color.WHITE, whitePieces, Color.BLACK, blackPieces);
        return board;
    }

    private static Map<Square, PieceKind> getClassicalPositionsForMainPieces(final boolean isWhite) {
        final int row = isWhite ? 1 : 8;
        return new HashMap<>(Map.of(
                new Square(1, row), PieceKind.ROOK,
                new Square(2, row), PieceKind.KNIGHT,
                new Square(3, row), PieceKind.BISHOP,
                new Square(4, row), PieceKind.QUEEN,
                new Square(5, row), PieceKind.KING,
                new Square(6, row), PieceKind.BISHOP,
                new Square(7, row), PieceKind.KNIGHT,
                new Square(8, row), PieceKind.ROOK
        ));
    }

    public static boolean isInBoard(final Square square) {
        return isColumnInBoard(square.getColumn()) && isRowInBoard(square.getRow());
    }

    public static boolean isColumnInBoard(final int i) {
        return i >= 1 && i <= Constants.BOARD_COLS;
    }

    public static boolean isRowInBoard(final int j) {
        return j >= 1 && j <= Constants.BOARD_ROWS;
    }

    public void addPiece(final Square square, final Piece piece) {
        final Piece oldPiece = this.pieces.get(piece.getColor().getIndex()).put(square, piece);
        if (oldPiece != null) {
            log.warning("The following piece was already on the board! " + oldPiece);
        }
    }

    public void addPieces(final Color color1, final Map<Square, PieceKind> squarePieces1, final Color color2, final Map<Square, PieceKind> squarePieces2) {
        for (Map.Entry<Square, PieceKind> square : squarePieces1.entrySet()) {
            this.addPiece(square.getKey(), new Piece(color1, square.getValue()));
        }
        for (Map.Entry<Square, PieceKind> square : squarePieces2.entrySet()) {
            this.addPiece(square.getKey(), new Piece(color2, square.getValue()));
        }
    }

    public void printBoard() {
        final List<List<Piece>> piecesAtEachRow = this.getPiecesAtEachRow();
        System.out.println(" |a|b|c|d|e|f|g|h|");
        System.out.println(" |---------------|");
        int rowIndex = Constants.BOARD_ROWS;
        for (List<Piece> piecesAtRow : piecesAtEachRow.reversed()) {
            System.out.print(rowIndex);
            char sep = '|';
            for (Piece piece : piecesAtRow) {
                System.out.print(sep + piece.printOnBoard());
                sep = ' ';
            }
            rowIndex--;
            System.out.println("|");
        }
        System.out.println(" |---------------|");
        System.out.println(" |a|b|c|d|e|f|g|h|");
    }

    private List<List<Piece>> getPiecesAtEachRow() {
        final List<List<Piece>> piecesAtEachRow = new ArrayList<>(Constants.BOARD_ROWS);
        for (int i = 1; i <= Constants.BOARD_ROWS; i++) {
            final List<Piece> piecesAtRow = new ArrayList<>(Constants.BOARD_COLS);
            for (int j = 1; j <= Constants.BOARD_COLS; j++) {
                Piece piece = this.pieces.get(Color.WHITE.getIndex()).get(new Square(j, i));
                if (piece == null) {
                    piece = this.pieces.get(Color.BLACK.getIndex()).get(new Square(j, i));
                }
                if (piece == null) {
                    // No piece at the given square
                    piece = new Piece(null, null);
                }
                piecesAtRow.add(piece);
            }
            piecesAtEachRow.add(piecesAtRow);
        }
        return piecesAtEachRow;
    }
}
