package com.sylvain.chess.play;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.io.fen.FenLoader;
import com.sylvain.chess.moves.Move;
import com.sylvain.chess.play.players.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class GameHistory {
  @Setter
  private String initialFen;
  @Getter
  private final PlayerColor firstPlayingColor;
  @Setter
  private List<Player> players;
  private final List<Move> moves =  new ArrayList<>();
  @Getter
  private final List<String> movesSan = new ArrayList<>();

  public GameHistory(final PlayerColor firstPlayingColor) {
    this.firstPlayingColor = firstPlayingColor;
  }

  public void addMove(final Move move) {
    this.moves.add(move);
    this.movesSan.add(move.toCompleteSan(false));
  }

  public int getFirstMoveNumber() {
    return FenLoader.getMoveNumber(this.initialFen);
  }

  public Player getFirstPlayerOfColor(final PlayerColor color) {
    return this.players.stream().filter(p -> p.getColor().equals(color)).findFirst().orElse(null);
  }
}
