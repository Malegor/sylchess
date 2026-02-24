package com.sylvain.chess.play.players.interactive;

import com.sylvain.chess.PlayerColor;
import com.sylvain.chess.board.ChessBoard;

import javax.swing.*;
import java.util.concurrent.CountDownLatch;

public class GuiPlayer extends InteractivePlayer {
  private final JTextField moveField;
  private CountDownLatch latch;
  private String move;

  public GuiPlayer(final PlayerColor color, final String name, final ChessBoard board, final JTextField moveField, final JButton submitButton) {
    super(color, name, board);
    this.moveField = moveField;
    this.latch = new CountDownLatch(1);
    submitButton.addActionListener(e -> {
      this.move = this.moveField.getText();
      this.latch.countDown();
    });
  }

  @Override
  protected String getNextMove() {
    try {
      this.latch.await();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
    }
    this.latch = new CountDownLatch(1);
    return this.move;
  }
}
