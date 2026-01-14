package com.sylvain.chess.play;

import com.sylvain.chess.Color;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.pieces.King;
import com.sylvain.chess.pieces.PieceOnBoard;
import com.sylvain.chess.play.players.Player;
import com.sylvain.chess.utils.CircularIterator;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lombok.Setter;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Gameplay {
  @Getter
  private final ChessBoard board;
  private final List<Player> players;
  private final int maxNumberOfMovesWithoutCaptureOrPawnMove;
  private final int maxNumberOfTimesSamePosition;
  @Getter
  private Player lastPlayer;
  @Getter @Setter
  private int moveNumber;
  @Getter
  private int halfMoveNumber;
  @Getter @Setter
  private int lastHalfMoveWithCaptureOrPawn;
  private final Map<String, List<Integer>> occurrencesOfPosition;
  private final Color firstPlayingColor;
  @Getter
  private EndGame endGame;

  public Gameplay(final ChessBoard board, final List<Player> players, final Color firstPlayingColor, final int maxNumberOfMovesWithoutCaptureOrPawnMove, final int maxNumberOfTimesSamePosition) {
    this.board = board;
    this.players = players;
    this.maxNumberOfMovesWithoutCaptureOrPawnMove = maxNumberOfMovesWithoutCaptureOrPawnMove;
    this.maxNumberOfTimesSamePosition = maxNumberOfTimesSamePosition;
    this.moveNumber = 1;
    this.halfMoveNumber = 1;
    this.lastHalfMoveWithCaptureOrPawn = 1;
    this.occurrencesOfPosition = new HashMap<>(20);
    this.lastPlayer = players.getLast();
    this.firstPlayingColor = firstPlayingColor;
    this.endGame = null;
  }

  public Gameplay(final ChessBoard board, final List<Player> players, final Color firstPlayingColor) {
    this(board, players, firstPlayingColor, 50, 3);
  }

  public Gameplay(final ChessBoard board, final List<Player> players) {
    this(board, players, null);
  }

  public GameStatus playGame() {
    return playGame(1000);
  }

  public GameStatus playGame(final int numberOfMoves) {
    final CircularIterator<Player> it = new CircularIterator<>(this.players);
    if (this.firstPlayingColor != null) {
      while (it.hasNext()) {
        if (it.peek().getColor() == this.firstPlayingColor) {
          break;
        }
        else this.lastPlayer = it.next();
      }
    }
    while (it.hasNext()) {
      final Player player = it.next();
      // OBS: small flaw here: in the rule, the en passant or castling possible moves should be considered for the repetition...
      // For example, if the rook hadn't moved before the first occurrence and then moved before the second one, the repeated position would not really a repetition.
      // Considering it would complicate a lot the validation and in practice it is not essential for most applications of the rule.
      final List<Integer> positionRepetitions = this.occurrencesOfPosition.computeIfAbsent(player.getColor() + ";" + this.board.getPositionString(), k -> new ArrayList<>(2));
      positionRepetitions.add(this.moveNumber);
      if (positionRepetitions.size() >= this.maxNumberOfTimesSamePosition) {
        log.info("Same position has already been repeated! {}", positionRepetitions);
        this.endGame = EndGame.DRAW;
        return GameStatus.SEVERAL_TIMES_SAME_POSITION;
      }
      if (this.moveNumber >= numberOfMoves)
        return GameStatus.PLAYING;
      if (this.halfMoveNumber - this.lastHalfMoveWithCaptureOrPawn > 2 * this.maxNumberOfMovesWithoutCaptureOrPawnMove) {
        log.info("{} moves have been played without any improvement! (since half move {})", this.maxNumberOfMovesWithoutCaptureOrPawnMove, this.lastHalfMoveWithCaptureOrPawn);
        this.endGame = EndGame.DRAW;
        return GameStatus.UNIMPROVING_MOVES;
      }
      this.lastPlayer = player;
      final Move move = player.move();
      if (move != null) {
        move.apply();
        log.info("{} - {}", this.moveNumber, move);
        this.board.printBoard();
        this.board.validateInternalDataStructures();
        if (move.involvesPawnOrCapture()) {
          this.lastHalfMoveWithCaptureOrPawn = this.halfMoveNumber;
          // TODO: uncomment next line in the case memory is needed
          //this.occurrencesOfPosition.clear();
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
        this.endGame = gameStatus.equals(GameStatus.STALEMATE) ? EndGame.DRAW : player.getColor().equals(Color.WHITE) ? EndGame.BLACK_WINS : EndGame.WHITE_WINS;
        return gameStatus;
      }
      this.halfMoveNumber++;
      // OBS: the following condition only works if the game doesn't exclude players (ex: in a chess game of 3 or more players)
      if (player.equals(players.getLast()))
        this.moveNumber++;
    }
    this.endGame = EndGame.ERROR;
    throw new IllegalStateException("Error! No more players can play.");
  }

  private boolean onlyKingsOnBoard() {
    for (final Player player : this.players) {
      Collection<PieceOnBoard> playerPieces = this.board.getPieces(player.getColor()).values();
      for (PieceOnBoard piece : playerPieces)
        if (!piece.getName().equals(King.NAME_LC)) {
          return false;
        }
    }
    return true;
  }
}
