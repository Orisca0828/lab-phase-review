package com.liziyan.minesweeper.model;

import java.util.ArrayList;
import java.util.List;

/**
 * 棋盘模型：负责网格数据、地雷摆放和邻雷计数，不处理任何输入输出。
 * <p>外部只能通过本类的方法修改格子状态，保证数据封装性。</p>
 */
public final class Board {
    private static final int[][] DIRECTIONS = {
            {-1, -1}, {-1, 0}, {-1, 1},
            {0, -1}, {0, 1},
            {1, -1}, {1, 0}, {1, 1}
    };

    private final int rows;
    private final int cols;
    private final int mineCount;
    private final Cell[][] cells;

    public Board(int rows, int cols, int mineCount) {
        if (rows <= 0 || cols <= 0) {
            throw new IllegalArgumentException("棋盘行列数必须大于 0");
        }
        if (mineCount < 0 || mineCount >= rows * cols) {
            throw new IllegalArgumentException("雷数必须小于总格子数");
        }
        this.rows = rows;
        this.cols = cols;
        this.mineCount = mineCount;
        this.cells = new Cell[rows][cols];
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                cells[r][c] = new Cell();
            }
        }
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public int mineCount() {
        return mineCount;
    }

    public int totalCells() {
        return rows * cols;
    }

    public boolean inBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public boolean inBounds(Position position) {
        return inBounds(position.row(), position.col());
    }

    public Cell cellAt(int row, int col) {
        return cells[row][col];
    }

    public Cell cellAt(Position position) {
        return cellAt(position.row(), position.col());
    }

    public boolean isMine(Position position) {
        return cellAt(position).isMine();
    }

    public boolean isRevealed(Position position) {
        return cellAt(position).isRevealed();
    }

    public boolean isFlagged(Position position) {
        return cellAt(position).isFlagged();
    }

    /** 返回坐标周围 8 个格子的坐标（自动过滤越界位置）。 */
    public List<Position> neighbours(Position position) {
        List<Position> result = new ArrayList<>(8);
        for (int[] direction : DIRECTIONS) {
            int row = position.row() + direction[0];
            int col = position.col() + direction[1];
            if (inBounds(row, col)) {
                result.add(new Position(row, col));
            }
        }
        return result;
    }

    public void setMine(Position position, boolean mine) {
        cellAt(position).setMine(mine);
    }

    public void setAdjacentMines(Position position, int count) {
        cellAt(position).setAdjacentMines(count);
    }

    public void reveal(Position position) {
        cellAt(position).setRevealed(true);
    }

    public void flag(Position position, boolean flagged) {
        cellAt(position).setFlagged(flagged);
    }

    /** 踩雷后把所有地雷亮出来，方便玩家核对。 */
    public void revealAllMines() {
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                if (cells[r][c].isMine()) {
                    cells[r][c].setRevealed(true);
                }
            }
        }
    }
}
