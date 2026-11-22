package com.liziyan.minesweeper.model;

import java.util.Objects;

/**
 * 棋盘坐标，行列均从 0 开始。
 * <p>控制台输入使用从 1 开始的坐标，由 {@code CommandParser} 负责转换，避免业务层到处 -1。</p>
 */
public final class Position {
    private final int row;
    private final int col;

    public Position(int row, int col) {
        this.row = row;
        this.col = col;
    }

    public int row() {
        return row;
    }

    public int col() {
        return col;
    }

    /** 返回偏移后的新坐标，原对象不变。 */
    public Position offset(int deltaRow, int deltaCol) {
        return new Position(row + deltaRow, col + deltaCol);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Position position)) {
            return false;
        }
        return row == position.row && col == position.col;
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, col);
    }

    @Override
    public String toString() {
        return "(" + row + ", " + col + ")";
    }
}
