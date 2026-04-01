package com.sylvain.chess.pieces;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.moves.Move;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import javax.swing.Icon;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public abstract class PieceOnBoard {
    protected final PlayerColor color;
    protected Square square;
    @Setter
    protected boolean hasAlreadyMoved;

    public abstract List<Square> getControlledSquares(final ChessBoard board);

    public Character printOnBoard() {
        return this.color == null ? this.getName() : this.color.changeChar().apply(this.getName());
    }

    public abstract Character getName();

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

    public PieceOnBoard move(final int columnDiff, final int rowDiff) {
        return this.at(this.square.move(columnDiff, rowDiff));
    }

    private interface PieceFactory {
        PieceOnBoard create(final PlayerColor color, final Square square);
    }

    public static PieceOnBoard createPiece(final char pieceChar, final Square square) {
        final PlayerColor color = getColor(pieceChar);
        final Map<Character, PieceFactory> factories = Map.of(
                Pawn.NAME_LC, Pawn::new,
                Knight.NAME_LC, Knight::new,
                Bishop.NAME_LC, Bishop::new,
                Rook.NAME_LC, Rook::new,
                Queen.NAME_LC, Queen::new,
                King.NAME_LC, King::new
        );
        final PieceFactory factory = factories.get(Character.toLowerCase(pieceChar));
        if (factory == null)
            throw new IllegalArgumentException("Unknown piece character: " + pieceChar);
        return factory.create(color, square);
    }

    public static PlayerColor getColor(final char pieceChar) {
      return Character.isUpperCase(pieceChar) ? PlayerColor.WHITE : PlayerColor.BLACK;
    }

    public List<Move> findValidMoves(final ChessBoard board) {
        final List<Move> validMoves = new ArrayList<>();
        Set<Square> squares = this.getControlledSquares(board).stream().
                filter(square -> !board.hasPieceAt(square) || this.color != board.getPieceAt(square).getColor()).collect(Collectors.toSet());
        for (Square square : squares) {
            Move move = new Move(Map.of(this, this.at(square)), board);
            if (move.isValidMove()) validMoves.add(move);
        }
        return validMoves;
    }

    public Icon getIcon(final PlayerColor color) {
        return new javax.swing.ImageIcon(Objects.requireNonNull(getClass().getResource(this.getIconPath(color))));
    }

    public abstract String getIconPath(final PlayerColor color);
}
