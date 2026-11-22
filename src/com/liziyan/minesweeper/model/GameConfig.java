package com.liziyan.minesweeper.model;

/**
 * 一局游戏的基础配置：行数、列数、雷数。
 * <p>在构造时统一校验，保证进入游戏逻辑后数据一定合法，避免后续出现数组越界等运行时异常。</p>
 */
public record GameConfig(int rows, int cols, int mines) {
    public static final int MIN_SIZE = 5;
    public static final int MAX_SIZE = 50;

    public GameConfig {
        if (rows < MIN_SIZE || rows > MAX_SIZE) {
            throw new IllegalArgumentException("行数必须在 " + MIN_SIZE + "~" + MAX_SIZE + " 之间");
        }
        if (cols < MIN_SIZE || cols > MAX_SIZE) {
            throw new IllegalArgumentException("列数必须在 " + MIN_SIZE + "~" + MAX_SIZE + " 之间");
        }
        int total = rows * cols;
        if (mines < 1 || mines > total - 1) {
            throw new IllegalArgumentException("雷数必须在 1~" + (total - 1) + " 之间");
        }
    }

    public int totalCells() {
        return rows * cols;
    }

    public int safeCells() {
        return totalCells() - mines;
    }
}
