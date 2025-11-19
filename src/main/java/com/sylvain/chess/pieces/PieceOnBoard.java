package com.sylvain.chess.pieces;

import com.sylvain.chess.Constants;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class PieceOnBoard {
    final Piece piece;
    final int column;
    final int row;

    public boolean isValid() {
        return column >= 1 && column <= Constants.BOARD_COLS && row >= 1 && row <= Constants.BOARD_ROWS && piece.isValidAt(this.column, this.row);
    }

    /**
     * @return All the possible moves on the board, regardless of the position of other pieces.
     */
    //public List<PieceOnBoard> getMoveSpan();
}