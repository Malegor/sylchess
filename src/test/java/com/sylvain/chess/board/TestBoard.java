package com.sylvain.chess.board;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.pieces.Bishop;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.Knight;
import com.sylvain.chess.pieces.Pawn;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.pieces.Queen;
import com.sylvain.chess.pieces.Rook;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class TestBoard {
    @Test
    public void testBoardLimits() {
        Assert.assertFalse(ChessBoard.isInBoard(new Square(0, 5)));
        Assert.assertFalse(ChessBoard.isInBoard(new Square(2, ChessBoard.BOARD_ROWS + 1)));
        Assert.assertTrue(ChessBoard.isInBoard(new Square(1, 1)));
    }

    @Test
    public void testPrintBoard() {
        final ChessBoard board = new ChessBoard();
        board.addPiece(new Rook(PlayerColor.WHITE, new Square(1, 1)));
        board.addPiece(new King(PlayerColor.BLACK, new Square(5, 8)));
        board.printBoard();
    }

    @Test
    public void testGetSquare() {
        final ChessBoard board = new ChessBoard();
        Assert.assertThrows(IllegalArgumentException.class, () -> board.getSquare(""));
        Assert.assertThrows(IllegalArgumentException.class, () -> board.getSquare("a3a"));
        Assert.assertThrows(IllegalArgumentException.class, () -> board.getSquare("a9"));
        Assert.assertThrows(IllegalArgumentException.class, () -> board.getSquare("j1"));
        Assert.assertEquals(new Square(1, 1),  board.getSquare("a1"));
        Assert.assertEquals(new Square(8, 8),  board.getSquare("h8"));
        Assert.assertEquals(new Square(5, 1),  board.getSquare("e1"));
    }

    @Test
    public void testClassicalBoard() {
        final ChessBoard board = ChessBoard.defaultBoard();
        board.printBoard();
        this.assertValidInitialBoard(board, PlayerColor.WHITE);
        this.assertValidInitialBoard(board, PlayerColor.BLACK);
        this.assertSamePositionsForBothColors(board);
    }

    @Test
    public void testChess960() {
        final ChessBoard board = ChessBoard.get960BoardBySeed(null);
        board.printBoard();
        this.assertValidInitialBoard(board, PlayerColor.WHITE);
        this.assertValidInitialBoard(board, PlayerColor.BLACK);
        this.assertSamePositionsForBothColors(board);
    }

    private void assertSamePositionsForBothColors(final ChessBoard board) {
        final Map<Square, PieceOnBoard> whitePieces = board.getPieces(PlayerColor.WHITE);
        final Map<Square, PieceOnBoard> blackPieces = board.getPieces(PlayerColor.BLACK);
        Assert.assertEquals(whitePieces.size(), blackPieces.size());
        for (final PieceOnBoard pob : whitePieces.values()) {
            final Square square = pob.getSquare();
            Assert.assertEquals(pob.getName(), blackPieces.get(new Square(square.column(), ChessBoard.BOARD_ROWS - square.row() + 1)).getName());
        }
    }

    private void assertValidInitialBoard(final ChessBoard board, final PlayerColor color) {
        final Map<Square, PieceOnBoard> pieces = board.getPieces(color);
        Assert.assertEquals(16, pieces.size());
        for (Map.Entry<Square, PieceOnBoard> entry : pieces.entrySet()) {
            Assert.assertEquals(color, entry.getValue().getColor());
            Assert.assertEquals(entry.getKey(), entry.getValue().getSquare());
        }
        final int firstRow = color.equals(PlayerColor.WHITE) ? 1 : 8;
        final int secondRow = color.equals(PlayerColor.WHITE) ? 2 : 7;
        final Set<Pawn> pawns = pieces.values().stream().filter(pob -> pob.getName().equals(Pawn.NAME_LC)).map(Pawn.class::cast).collect(Collectors.toSet());
        Assert.assertEquals(8, pawns.size());
        for (final Pawn pawn : pawns) {
            Assert.assertEquals(secondRow, pawn.getSquare().row());
        }
        final Set<PieceOnBoard> mainPieces = new HashSet<>(pieces.values());
        mainPieces.removeAll(pawns);
        for (final PieceOnBoard piece : mainPieces) {
            Assert.assertEquals(firstRow, piece.getSquare().row());
        }
        final Set<Bishop> bishops = mainPieces.stream().filter(pob -> pob.getName().equals(Bishop.NAME_LC)).map(Bishop.class::cast).collect(Collectors.toSet());
        Assert.assertEquals(2, bishops.size());
        // One bishop on a dark square, the other one on a light square.
        Assert.assertEquals(1, bishops.stream().map(b -> b.getSquare().column()).mapToInt(Integer::intValue).sum() % 2);
        final Set<Knight> knights = mainPieces.stream().filter(pob -> pob.getName().equals(Knight.NAME_LC)).map(Knight.class::cast).collect(Collectors.toSet());
        Assert.assertEquals(2, knights.size());
        final Set<Queen> queens = mainPieces.stream().filter(pob -> pob.getName().equals(Queen.NAME_LC)).map(Queen.class::cast).collect(Collectors.toSet());
        Assert.assertEquals(1, queens.size());
        final Set<King> kings = mainPieces.stream().filter(pob -> pob.getName().equals(King.NAME_LC)).map(King.class::cast).collect(Collectors.toSet());
        Assert.assertEquals(1, kings.size());
        final Set<Rook> rooks = mainPieces.stream().filter(pob -> pob.getName().equals(Rook.NAME_LC)).map(Rook.class::cast).collect(Collectors.toSet());
        Assert.assertEquals(2, rooks.size());
        final int kingColumn = kings.iterator().next().getSquare().column();
        final int rookMinColumn = rooks.stream().map(r -> r.getSquare().column()).mapToInt(Integer::intValue).min().orElse(500);
        final int rookMaxColumn = rooks.stream().map(r -> r.getSquare().column()).mapToInt(Integer::intValue).max().orElse(-1);
        Assert.assertTrue(kingColumn > rookMinColumn);
        Assert.assertTrue(kingColumn < rookMaxColumn);
    }
}
