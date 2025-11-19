package com.sylvain.chess.board;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ChessBoard {
    final List<PieceOnBoard> whitePieces;
    final List<PieceOnBoard> blackPieces;
}
