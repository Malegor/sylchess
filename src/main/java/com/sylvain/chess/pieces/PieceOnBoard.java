package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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

    private interface PieceFactory {
        PieceOnBoard create(final Color color, final Square square);
    }

    public static PieceOnBoard createPiece(final char pieceChar, final Square square) {
        final Color color =  Character.isUpperCase(pieceChar) ? Color.WHITE : Color.BLACK;
        final Map<Character, PieceFactory> factories = Map.of(
                'p', Pawn::new,
                'n', Knight::new,
                'b', Bishop::new,
                'r', Rook::new,
                'q', Queen::new,
                'k', King::new
        );
        final PieceFactory factory = factories.get(Character.toLowerCase(pieceChar));
        if (factory == null)
            throw new IllegalArgumentException("Unknown piece character: " + pieceChar);
        return factory.create(color, square);
    }
}
