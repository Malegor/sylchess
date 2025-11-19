package com.sylvain.chess.board;

import com.sylvain.chess.pieces.PieceOnBoard;
import lombok.AllArgsConstructor;

import java.util.List;

@AllArgsConstructor
public class ChessBoard {
    final List<PieceOnBoard> whitePieces;
    final List<PieceOnBoard> blackPieces;
}
