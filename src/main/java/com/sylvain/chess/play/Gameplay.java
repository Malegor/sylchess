package com.sylvain.chess.play;

import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.play.players.Player;
import com.sylvain.chess.utils.CircularIterator;
import lombok.Getter;
import lombok.extern.java.Log;

import java.util.List;
import java.util.logging.Level;

@Log
public class Gameplay {
  private final ChessBoard board;
  private final List<Player> players;
  @Getter
  private Player lastPlayer;

  public Gameplay(ChessBoard board, List<Player> players) {
    this.board = board;
    this.players = players;
  }

  public GameStatus playGame() {
    final CircularIterator<Player> it = new CircularIterator<>(this.players);
    while (it.hasNext()) {
      Player player = it.next();
      this.lastPlayer = player;
      final Move move = player.move(this.board);
      if (move != null) {
        move.apply();
      }
      else {
        // OBS: in case of checkmate, remove the player and continue with the other ones? (ex: chess with 4 players)
        return !this.board.getPieces(player.getColor()).isEmpty() && !this.isKingUnderCheck(player) ? GameStatus.STALEMATE : GameStatus.CHECKMATE;
      }
      final GameStatus status = this.conditionForEndGame(move, player);
      if (status != null && status != GameStatus.PLAYING)
        return status;
    }
    log.log(Level.SEVERE, "Error! No more players");
    return null;
  }

  private boolean isKingUnderCheck(final Player player) {
    return !this.board.piecesCheckingKing(player.getColor()).isEmpty();
  }

  private GameStatus conditionForEndGame(final Move lastMove, final Player player) {
    if (lastMove == null) {
      // Checkmate or Stalemate situation
      return null; // TODO: distinguish checkmate and stalemate (in this case, no winner)
    }
    // TODO: validations?
    return null;
  }
}
