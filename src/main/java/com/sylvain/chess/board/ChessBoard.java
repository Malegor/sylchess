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
        return square.getColumn() >= 1 && square.getColumn() <= Constants.BOARD_COLS && square.getRow() >= 1 && square.getRow() <= Constants.BOARD_ROWS;
    }
}
