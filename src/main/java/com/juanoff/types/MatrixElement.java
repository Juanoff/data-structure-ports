package com.juanoff.types;

public class MatrixElement {
    public int x, y;
    public double value;

    public MatrixElement(int x, int y, double value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    @Override
    public String toString() {
        return String.format("(%d, %d) = %.2f", x, y, value);
    }
}
