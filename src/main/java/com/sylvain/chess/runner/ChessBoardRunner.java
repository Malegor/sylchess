package com.sylvain.chess.runner;

import com.sylvain.chess.ui.BoardFrame;

import javax.swing.SwingUtilities;

public class ChessBoardRunner {

  public static void main(String[] args) {
    // Ensure GUI creation happens on the Event Dispatch Thread (EDT)
    SwingUtilities.invokeLater(BoardFrame::new);
  }
}