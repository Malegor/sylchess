package com.sylvain.chess.board;

import com.sylvain.chess.Constants;
import com.sylvain.chess.pieces.PieceOnBoard;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ChessBoard {
    private final List<PieceOnBoard> whitePieces;
    private final List<PieceOnBoard> blackPieces;

    public static boolean isInBoard(final Square square) {
        return isColumnInBoard(square.getColumn()) && isRowInBoard(square.getRow());
    }

    public static boolean isColumnInBoard(final int i) {
        return i >= 1 && i <= Constants.BOARD_COLS;
    }

    public static boolean isRowInBoard(final int j) {
        return j >= 1 && j <= Constants.BOARD_ROWS;
    }
}
