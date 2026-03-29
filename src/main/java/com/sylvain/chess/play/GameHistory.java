package com.sylvain.chess.play;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.moves.Move;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public class GameHistory {
  @Setter
  private String initialFen;
  @Getter
  private final PlayerColor firstPlayingColor;
  private final List<Move> moves =  new ArrayList<>();
  private final List<String> movesSan = new ArrayList<>();

  public GameHistory(final PlayerColor firstPlayingColor) {
    this.firstPlayingColor = firstPlayingColor;
  }

  public void addMove(final Move move) {
    this.moves.add(move);
    this.movesSan.add(move.toSan());
  }
}
