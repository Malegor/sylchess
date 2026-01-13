package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.board.Square;
import com.sylvain.chess.moves.Move;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Pawn extends PieceOnBoard {
    public static char NAME_LC = 'p';

    public Pawn(final Color color, final Square startingSquare) {
        this(color, startingSquare, false);
    }

    public Pawn(final Color color, final Square square, final boolean hasAlreadyMoved) {
        super(color, square, hasAlreadyMoved);
    }

    @Override
    public Pawn at(final Square square) {
        return new Pawn(this.color, square, this.hasAlreadyMoved);
    }
    public Rook toRook(final Square square) {
        return new Rook(this.color, square, this.hasAlreadyMoved);
    }
    public Bishop toBishop(final Square square) {
        return new Bishop(this.color, square, this.hasAlreadyMoved);
    }
    public Knight toKnight(final Square square) {
        return new Knight(this.color, square, this.hasAlreadyMoved);
    }
    public Queen toQueen(final Square square) {return new Queen(this.color, square, this.hasAlreadyMoved);}

    @Override
    public List<Square> getControlledSquares(final ChessBoard board) {
        final List<Square> controlled = new ArrayList<>(2);
        final int updateRow = ChessBoard.getPawnDirection(color);
        if (square.column() > 1) controlled.add(square.move(-1, updateRow));
        if (square.column() < ChessBoard.BOARD_COLS) controlled.add(square.move(1, updateRow));
        return controlled;
    }

    @Override
    public Character getName() {
        return NAME_LC;
    }

    @Override
    public boolean isPossiblePromotion() {
        return false;
    }

    @Override
    public List<Move> findValidMoves(final ChessBoard board) {
        final List<Move> validMoves = new ArrayList<>(4);
        for (int incrementRow = 1; incrementRow <= 2; incrementRow++) {
            for (int incrementCol = -1 ; incrementCol <= 1 ; incrementCol++) {
                final Square newSquare = this.square.move(incrementCol, incrementRow * ChessBoard.getPawnDirection(this.color));
                if (ChessBoard.isInBoard(newSquare)) {
                    if (newSquare.row() != ChessBoard.getPromotionRow(this.color)) {
                        Move possibleMove = new Move(Map.of(this, this.at(newSquare)), board);
                        if (possibleMove.isValidMove()) {
                            validMoves.add(possibleMove);
                        }
                    }
                    else {
                        // Promotion
                        final Move promoToQueen = new Move(Map.of(this, this.toQueen(newSquare)), board);
                        if (promoToQueen.isValidMove()) {
                            validMoves.add(promoToQueen);
                            validMoves.add(new Move(Map.of(this, this.toKnight(newSquare)), board));
                            validMoves.add(new Move(Map.of(this, this.toRook(newSquare)), board));
                            validMoves.add(new Move(Map.of(this, this.toBishop(newSquare)), board));
                        }
                    }
                }
            }
        }
        return validMoves;
    }
}
