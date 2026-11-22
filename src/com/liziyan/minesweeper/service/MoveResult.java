package com.liziyan.minesweeper.service;

/**
 * 一次操作的结果。业务层只返回结果，具体提示文字交给界面层展示。
 */
public enum MoveResult {
    OK("操作成功"),
    HIT_MINE("踩到地雷了"),
    WON("所有安全格子都已翻开"),
    ALREADY_REVEALED("该格子已经翻开过了"),
    FLAGGED("该格子插着旗，请先取消插旗再翻开"),
    ALREADY_FLAGGED("该格子已经插旗"),
    FLAG_LIMIT("旗子数量已用完，请先取消一面旗"),
    OUT_OF_BOUNDS("坐标超出棋盘范围"),
    GAME_FINISHED("本局已经结束");

    private final String message;

    MoveResult(String message) {
        this.message = message;
    }

    public String message() {
        return message;
    }
}
