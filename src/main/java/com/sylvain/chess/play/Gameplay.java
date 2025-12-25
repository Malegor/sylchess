package com.sylvain.chess.play;

import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.play.players.Player;
import com.sylvain.chess.utils.CircularIterator;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

@Log
public class Gameplay {
  private final ChessBoard board;
  private final List<Player> players;
  private final int numberOfMovesWithoutCaptureOrPawnMove;
  private final int maxNumberOfTimesSamePosition;
  @Getter
  private Player lastPlayer;
  @Getter
  private int moveNumber;
  private int lastMoveWithCaptureOrPawn;
  private final Map<String, List<Integer>> occurrencesOfPosition;

  public Gameplay(final ChessBoard board, final List<Player> players, final int numberOfMovesWithoutCaptureOrPawnMove, final int maxNumberOfTimesSamePosition) {
    this.board = board;
    this.players = players;
    this.numberOfMovesWithoutCaptureOrPawnMove = numberOfMovesWithoutCaptureOrPawnMove;
    this.maxNumberOfTimesSamePosition = maxNumberOfTimesSamePosition;
    this.moveNumber = 0;
    this.lastMoveWithCaptureOrPawn = 1;
    this.occurrencesOfPosition = new HashMap<>(20);
    this.lastPlayer = players.getLast();
  }

  public Gameplay(final ChessBoard board, final List<Player> players) {
    this(board, players, 50, 3);
  }

  public GameStatus playGame() {
    final CircularIterator<Player> it = new CircularIterator<>(this.players);
    while (it.hasNext()) {
      Player player = it.next();
      // OBS: small flaw here: in the rule, the en passant or castling possible moves should be considered for the repetition...
      // For example, if the rook hadn't moved before the first occurrence and then moved before the second one, the repeated position would not really a repetition.
      // Considering it would complicate a lot the validation and in practice it is not essential for most applications of the rule.
      final List<Integer> positionRepetitions = this.occurrencesOfPosition.computeIfAbsent(player.getColor() + ";" + this.board.getPositionString(), k -> new ArrayList<>(2));
      positionRepetitions.add(this.moveNumber);
      if (positionRepetitions.size() >= this.maxNumberOfTimesSamePosition) {
        log.info("Same position has already been repeated! " + positionRepetitions);
        return GameStatus.SEVERAL_TIMES_SAME_POSITION;
      }
      // OBS: the following condition only works if the game doesn't exclude players (ex: in a chess game of 3 or more players)
      if (player.equals(players.getFirst()))
        this.moveNumber++;
      if (this.moveNumber - this.lastMoveWithCaptureOrPawn >  this.numberOfMovesWithoutCaptureOrPawnMove) {
        log.info(this.numberOfMovesWithoutCaptureOrPawnMove + " moves have been played without any improvement! (since move " + this.lastMoveWithCaptureOrPawn + ")");
        return GameStatus.UNIMPROVING_MOVES;
      }
      this.lastPlayer = player;
      final Move move = player.move(this.board);
      if (move != null) {
        move.apply();
        log.info(this.moveNumber + " - " + move);
        this.board.printBoard();
        if (move.involvesPawnOrCapture()) {
          this.lastMoveWithCaptureOrPawn = this.moveNumber;
          // TODO: uncomment next line in the case memory is needed
          //this.occurrencesOfPosition.clear();
        }
      }
      else {
        // OBS: in case of checkmate, remove the player and continue with the other ones? (ex: chess with 3 or 4 players)
        return !this.board.getPieces(player.getColor()).isEmpty() && !this.isKingUnderCheck(player) ? GameStatus.STALEMATE : GameStatus.CHECKMATE;
      }
    }
    log.log(Level.SEVERE, "Error! No more players can play.");
    return null;
  }

  private boolean isKingUnderCheck(final Player player) {
    return !this.board.piecesCheckingKing(player.getColor()).isEmpty();
  }
}
