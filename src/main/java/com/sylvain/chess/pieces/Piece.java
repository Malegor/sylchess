package com.sylvain.chess.pieces;

import com.sylvain.chess.Color;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class Piece {
    private final Color color;
    private final PieceKind pieceKind;

    public String printOnBoard() {
        return this.color == null || this.pieceKind == null ? "." : this.color == Color.WHITE ? pieceKind.printOnBoard().toUpperCase() : pieceKind.printOnBoard().toLowerCase();
    }
}