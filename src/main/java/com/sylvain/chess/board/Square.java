package com.sylvain.chess.board;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Objects;

@AllArgsConstructor
@Getter
public class Square implements Comparable<Square> {
    private final int column;
    private final int row;

    public Square move(final int column, final int row) {
        return new Square(this.column + column, this.row + row);
    }

    @Override
    public boolean equals(Object o) {
        final Square other = (Square) o;
        return this.column == other.column && this.row == other.row;
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.column, this.row);
    }

    @Override
    public String toString() {
        return String.valueOf(getColumnLetter(this.column)) + this.getRow();
    }

    private static char getColumnLetter(final int column) {
        // Check if the number is within the valid range (1-26).--> TODO: throw exception?
        // 'a' has an ASCII value of 97.
        // Subtracting 1 from the number gives a 0-based index.
        // Adding this index to 'a' gives the correct character.
        return column >= 1 && column <= 26 ? (char) ('a' + column - 1) : '?';
    }

    @Override
    public int compareTo(final Square other) {
        return  this.getRow() == other.getRow() ?  this.getColumn() - other.getColumn() : this.getRow() - other.getRow();
    }
}
