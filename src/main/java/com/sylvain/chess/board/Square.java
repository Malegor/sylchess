package com.sylvain.chess.board;

import com.sylvain.chess.Constants;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Square {
    final int column;
    final int row;

    public boolean isValid() {
        return this.column >= 1 && this.column <= Constants.BOARD_COLS && this.row >= 1 && this.row <= Constants.BOARD_ROWS;
    }
}
