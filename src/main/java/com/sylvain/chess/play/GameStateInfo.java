package com.sylvain.chess.play;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;
import com.sylvain.chess.play.players.Player;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameStateInfo {
  @Getter @Setter
  private Player lastPlayer;
  @Getter @Setter
  private int moveNumber;
  @Getter @Setter
  private int halfMoveNumber;
  @Getter @Setter
  private int lastHalfMoveWithCaptureOrPawn;
  private final Map<String, List<Integer>> occurrencesOfPosition;


  public GameStateInfo() {
    this.moveNumber = 1;
    this.halfMoveNumber = 1;
    this.lastHalfMoveWithCaptureOrPawn = 1;
    this.occurrencesOfPosition = new HashMap<>(20);
  }

  public List<Integer> newPosition(final PlayerColor color, final ChessBoard board) {
    final List<Integer> moves = this.occurrencesOfPosition.computeIfAbsent(color + ";" + board.getPositionString(), k -> new ArrayList<>(2));
    moves.add(this.moveNumber);
    return moves;
  }

  public void movedPawnOrCaptured() {
    this.lastHalfMoveWithCaptureOrPawn = this.halfMoveNumber;
  }

  public void incrementMove() {
    this.moveNumber++;
  }

  public void incrementHalfMove() {
    this.halfMoveNumber++;
  }
}
