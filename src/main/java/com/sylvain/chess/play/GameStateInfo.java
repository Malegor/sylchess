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
import java.util.Set;
import java.util.stream.Collectors;

public class GameStateInfo {
  @Getter @Setter
  private Player lastPlayer;
  @Getter
  private int moveNumber;
  @Getter
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
    final List<Integer> moves = this.occurrencesOfPosition.computeIfAbsent(getPositionKey(color, board), k -> new ArrayList<>(2));
    moves.add(this.moveNumber);
    return moves;
  }

  public static String getPositionKey(final PlayerColor color, final ChessBoard board) {
    return color + ";" + board.getPositionString();
  }

  public void movedPawnOrCaptured() {
    this.lastHalfMoveWithCaptureOrPawn = this.halfMoveNumber;
    // TODO: uncomment next line in the case memory is needed
    //this.occurrencesOfPosition.clear();
  }

  public void incrementMove() {
    this.moveNumber++;
  }

  public void incrementHalfMove() {
    this.halfMoveNumber++;
  }

  public void setMoveNumber(final int moveNumber) {
    this.moveNumber = moveNumber;
    this.halfMoveNumber = 2 * moveNumber - 1;
  }

  public Set<String> getPositionsAlmostAtDraw(final DrawConditions conditions) {
    return this.occurrencesOfPosition.entrySet().stream().filter(e -> e.getValue().size() >= conditions.maxNumberOfTimesSamePosition() - 1)
            .map(Map.Entry::getKey).collect(Collectors.toSet());
  }
}
