package com.sylvain.chess.board;

import com.sylvain.chess.Constants;
import com.sylvain.chess.pieces.Piece;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PieceOnBoard {
    final Piece piece;
    final int column;
    final int row;

    public boolean isValid() {
        return column >= 1 && column <= Constants.BOARD_COLS && row >= 1 && row <= Constants.BOARD_ROWS && piece.isValidAt(this.column, this.row);
    }
}