package com.sylvain.chess.pieces;

import com.sylvain.chess.Constants;
import com.sylvain.chess.board.Square;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PieceOnBoard {
    final Piece piece;
    final Square square;

    public boolean isValid() {
        return this.square.isValid() && piece.isValidAt(this.square);
    }

    /**
     * @return All the possible moves on the board, regardless of the position of other pieces.
     */
    //public List<PieceOnBoard> getMoveSpan();
}