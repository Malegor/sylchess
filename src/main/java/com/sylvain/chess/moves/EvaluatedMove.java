package com.sylvain.chess.moves;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class EvaluatedMove {
  private final Move move;
  private final double evaluation;
}
