package com.sylvain.chess.play;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.io.fen.FenSaver;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.play.players.Player;
import com.sylvain.chess.utils.CircularIterator;
import lombok.Getter;

import java.util.Collection;
import java.util.List;

import lombok.extern.log4j.Log4j2;

@Log4j2
public class Gameplay {
  @Getter
  private final ChessBoard board;
  private final int maxNumberOfMovesWithoutCaptureOrPawnMove;
  private final int maxNumberOfTimesSamePosition;
  @Getter
  private final GameStateInfo info;
  @Getter
  private final GameHistory history;
  @Getter
  private EndGame endGame;

  public Gameplay(final ChessBoard board, final PlayerColor firstPlayingColor, final int maxNumberOfMovesWithoutCaptureOrPawnMove, final int maxNumberOfTimesSamePosition) {
    this.board = board;
    this.maxNumberOfMovesWithoutCaptureOrPawnMove = maxNumberOfMovesWithoutCaptureOrPawnMove;
    this.maxNumberOfTimesSamePosition = maxNumberOfTimesSamePosition;
    this.endGame = EndGame.PLAYING;
    this.info = new GameStateInfo();
    this.history = new GameHistory(firstPlayingColor);
  }

  public Gameplay(final ChessBoard board, final PlayerColor firstPlayingColor) {
    this(board, firstPlayingColor, 50, 3);
  }

  public Gameplay(final ChessBoard board) {
    this(board, PlayerColor.WHITE);
  }

  public GameStatus playGame(final List<Player> players) {
    return playGame(players, 1000);
  }

  public GameStatus playGame(final List<Player> players, final int maxNumberOfMoves) {
    this.info.setLastPlayer(players.getLast());
    this.history.setPlayers(players);
    final CircularIterator<Player> it = new CircularIterator<>(players);
    if (this.history.getFirstPlayingColor() != null) {
      while (it.hasNext()) {
        if (it.peek().getColor() == this.history.getFirstPlayingColor()) {
          break;
        }
        else this.info.setLastPlayer(it.next());
      }
    }
    this.history.setInitialFen(FenSaver.getPositionString(this));
    while (it.hasNext()) {
      final Player player = it.next();
      // OBS: small flaw here: in the rule, the en passant or castling possible moves should be considered for the repetition...
      // For example, if the rook hadn't moved before the first occurrence and then moved before the second one, the repeated position would not really a repetition.
      // Considering it would complicate a lot the validation and in practice it is not essential for most applications of the rule.
      final List<Integer> positionRepetitions = this.info.newPosition(player.getColor(), this.board);
      if (positionRepetitions.size() >= this.maxNumberOfTimesSamePosition) {
        log.info("Same position has already been repeated! {}", positionRepetitions);
        this.endGame = EndGame.DRAW;
        return GameStatus.SEVERAL_TIMES_SAME_POSITION;
      }
      if (this.info.getMoveNumber() >= maxNumberOfMoves)
        return GameStatus.PLAYING;
      if (this.info.getHalfMoveNumber() - this.info.getLastHalfMoveWithCaptureOrPawn() > 2 * this.maxNumberOfMovesWithoutCaptureOrPawnMove) {
        log.info("{} moves have been played without any improvement! (since half move {})", this.maxNumberOfMovesWithoutCaptureOrPawnMove, this.info.getLastHalfMoveWithCaptureOrPawn());
        this.endGame = EndGame.DRAW;
        return GameStatus.UNIMPROVING_MOVES;
      }
      this.info.setLastPlayer(player);
      final Move move = player.getSelectedMove();
      if (move != null) {
        move.apply();
        log.info("{} - {}", this.info.getMoveNumber(), move);
        this.history.addMove(move);
        this.board.printBoard();
        this.board.validateInternalDataStructures();
        if (move.involvesPawnOrCapture()) {
          this.info.movedPawnOrCaptured();
        }
        if (this.onlyKingsOnBoard()) {
          this.endGame = EndGame.DRAW;
          return GameStatus.ONLY_KINGS;
        }
      }
      else {
        // OBS: in case of checkmate, remove the player and continue with the other ones? (ex: chess with 3 or 4 players)
        final boolean noValidMoves = this.board.findAllValidMoves(player.getColor()).isEmpty();
        final boolean isCheckmate = this.board.getPieces(player.getColor()).isEmpty() || this.board.isKingUnderCheck(player.getColor());
        final GameStatus gameStatus = !noValidMoves ? GameStatus.RESIGNED : isCheckmate ? GameStatus.CHECKMATE : GameStatus.STALEMATE;
        this.endGame = gameStatus.equals(GameStatus.STALEMATE) ? EndGame.DRAW : player.getColor().equals(PlayerColor.WHITE) ? EndGame.BLACK_WINS : EndGame.WHITE_WINS;
        return gameStatus;
      }
      this.info.incrementHalfMove();
      // OBS: the following condition only works if the game doesn't exclude players (ex: in a chess game of 3 or more players)
      if (player.equals(players.getLast()))
        this.info.incrementMove();
    }
    this.endGame = EndGame.ERROR;
    throw new IllegalStateException("Error! No more players can play.");
  }

  private boolean onlyKingsOnBoard() {
    for (final PlayerColor color : this.board.getColors()) {
      final Collection<PieceOnBoard> playerPieces = this.board.getPieces(color).values();
      for (PieceOnBoard piece : playerPieces)
        if (!piece.getName().equals(King.NAME_LC)) {
          return false;
        }
    }
    return true;
  }
}
