package com.sylvain.chess.board;

public record Square(int column, int row) implements Comparable<Square> {
    public Square move(final int column, final int row) {
        return new Square(this.column + column, this.row + row);
    }

    @Override
    public boolean equals(Object o) {
        final Square other = (Square) o;
        return this.column == other.column && this.row == other.row;
    }

    @Override
    public String toString() {
        return String.valueOf(getColumnLetter()) + this.row();
    }

    public char getColumnLetter() {
        // Check if the number is within the valid range (1-26).--> TODO: throw exception?
        // 'a' has an ASCII value of 97.
        // Subtracting 1 from the number gives a 0-based index.
        // Adding this index to 'a' gives the correct character.
        return column >= 1 && column <= 26 ? (char) ('a' + column - 1) : '?';
    }

    @Override
    public int compareTo(final Square other) {
        return this.row() == other.row() ? this.column() - other.column() : this.row() - other.row();
    }
}
