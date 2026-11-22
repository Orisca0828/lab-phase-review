package com.liziyan.minesweeper.model;

/**
 * 一局游戏的状态：进行中、胜利、失败。
 */
public enum GameState {
    PLAYING,
    WON,
    LOST;

    public boolean isFinished() {
        return this != PLAYING;
    }
}
