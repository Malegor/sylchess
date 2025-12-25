package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class PieceOnBoard {
    protected final Color color;
    protected Square square;
    @Setter
    private boolean hasAlreadyMoved;

    public PieceOnBoard(final Color color, final Square startingSquare) {
        this.color = color;
        this.square = startingSquare;
        this.hasAlreadyMoved = false;
    }

    public abstract List<Square> getControlledSquares(final ChessBoard board);

    public abstract char printOnBoard();

    protected List<Square> getControlledSquaresInSingleDirection(final ChessBoard board, final int incrementColumn, final int incrementRow) {
        final List<Square> controlled = new ArrayList<>(Math.max(ChessBoard.BOARD_COLS, ChessBoard.BOARD_ROWS) - 1);
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

    public abstract boolean isPossiblePromotion();

    public abstract PieceOnBoard at(final Square square);
}
