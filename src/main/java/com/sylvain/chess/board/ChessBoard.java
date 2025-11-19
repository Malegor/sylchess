package com.sylvain.chess.board;

import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ChessBoard {
    final List<Position> whitePieces;
    final List<Position> blackPieces;
}
