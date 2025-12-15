package com.sylvain.chess.board;

import com.sylvain.chess.Color;
import com.sylvain.chess.pieces.NoPiece;
import com.sylvain.chess.pieces.*;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Log
public class ChessBoard {
    public static final int BOARD_COLS = 8;
    public static final int BOARD_ROWS = 8;
    private final List<Map<Square, PieceOnBoard>> piecesByColor;
    private final Map<Square, PieceOnBoard> allPieces;

    public ChessBoard() {
        this.piecesByColor = List.of(new HashMap<>(16), new HashMap<>(16));
        this.allPieces = new HashMap<>(32);
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

    public void addPiece(final PieceOnBoard piece) {
        final PieceOnBoard oldPieceColor = this.piecesByColor.get(piece.getColor().getIndex()).put(piece.getSquare(), piece);
        final PieceOnBoard oldPiece = this.allPieces.put(piece.getSquare(), piece);
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
                PieceOnBoard piece = this.piecesByColor.get(Color.WHITE.getIndex()).get(new Square(j, i));
                if (piece == null) {
                    piece = this.piecesByColor.get(Color.BLACK.getIndex()).get(new Square(j, i));
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
        return this.allPieces.containsKey(square);
    }
}
