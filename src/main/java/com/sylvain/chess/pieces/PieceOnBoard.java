package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.HashSet;
import java.util.Set;

@AllArgsConstructor
@Getter
public abstract class PieceOnBoard {
    protected final Color color;
    protected Square square;

    public abstract Set<Square> getControlledSquares(final ChessBoard board);

    public abstract char printOnBoard();

    protected Set<Square> getControlledSquaresInSingleDirection(final ChessBoard board, final int incrementColumn, final int incrementRow) {
        final Set<Square> controlled = new HashSet<>(Math.max(ChessBoard.BOARD_COLS, ChessBoard.BOARD_ROWS) - 1);
        boolean foundPiece = false;
        Square newSquare = this.square.move(incrementColumn, incrementRow);
        while (!foundPiece && ChessBoard.isInBoard(newSquare)) {
            controlled.add(newSquare);
            foundPiece = board != null && board.hasPieceAt(newSquare);
            newSquare = newSquare.move(incrementColumn, incrementRow);
        }
        return controlled;
    }

    @Override
    public String toString() {
        return String.valueOf(this.printOnBoard()) + this.square;
    }
}
