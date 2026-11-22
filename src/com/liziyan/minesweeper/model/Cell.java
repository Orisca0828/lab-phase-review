package com.liziyan.minesweeper.model;

/**
 * 单个格子的数据模型，包含「是否地雷、是否翻开、是否插旗、周围雷数」四个状态。
 * <p>所有修改方法都是包级可见，只能被 {@link Board} 调用，防止业务层绕过棋盘直接改数据。</p>
 */
public final class Cell {
    private boolean mine;
    private boolean revealed;
    private boolean flagged;
    private int adjacentMines;

    public boolean isMine() {
        return mine;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public boolean isFlagged() {
        return flagged;
    }

    public int adjacentMines() {
        return adjacentMines;
    }

    /** 格子是否还盖着（未翻开）。 */
    public boolean isCovered() {
        return !revealed;
    }

    void setMine(boolean mine) {
        this.mine = mine;
    }

    void setRevealed(boolean revealed) {
        this.revealed = revealed;
    }

    void setFlagged(boolean flagged) {
        this.flagged = flagged;
    }

    void setAdjacentMines(int adjacentMines) {
        this.adjacentMines = adjacentMines;
    }
}
