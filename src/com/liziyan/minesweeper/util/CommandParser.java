package com.liziyan.minesweeper.util;

import com.liziyan.minesweeper.model.Position;

import java.util.Locale;

/**
 * 控制台指令解析器，把一行文本解析成「动作 + 坐标」。
 * <p>支持：{@code o 行 列}、{@code open 行 列}、{@code f 行 列}、{@code flag 行 列}、
 * 直接输入 {@code 行 列}（默认翻开）以及 {@code q}/{@code quit} 退出。坐标从 1 开始。</p>
 * <p>解析失败不会抛出异常，而是返回带错误提示的 {@link Command}，保证程序不会因为乱输入崩溃。</p>
 */
public final class CommandParser {
    public enum Action {
        OPEN,
        FLAG,
        QUIT,
        INVALID
    }

    /**
     * 解析结果：动作、坐标（退出或非法时为 null）、错误提示（合法时为 null）。
     */
    public record Command(Action action, Position position, String error) {
        public boolean isQuit() {
            return action == Action.QUIT;
        }

        public boolean isInvalid() {
            return action == Action.INVALID;
        }
    }

    private CommandParser() {
    }

    public static Command parse(String line, int rows, int cols) {
        if (line == null) {
            return new Command(Action.INVALID, null, "没有读到输入");
        }
        String trimmed = line.trim();
        if (trimmed.isEmpty()) {
            return new Command(Action.INVALID, null, "输入不能为空");
        }

        String[] parts = trimmed.split("\\s+");
        String head = parts[0].toLowerCase(Locale.ROOT);
        if (head.equals("q") || head.equals("quit")) {
            return new Command(Action.QUIT, null, null);
        }

        Action action = Action.OPEN;
        int coordinateIndex = 0;
        if (head.equals("o") || head.equals("open")) {
            coordinateIndex = 1;
        } else if (head.equals("f") || head.equals("flag")) {
            action = Action.FLAG;
            coordinateIndex = 1;
        } else if (parts.length != 2) {
            return new Command(Action.INVALID, null,
                    "指令格式应为：o 行 列 / f 行 列 / 直接输入 行 列");
        }

        if (parts.length - coordinateIndex != 2) {
            return new Command(Action.INVALID, null,
                    "指令格式应为：o 行 列 / f 行 列 / 直接输入 行 列");
        }

        int row;
        int col;
        try {
            row = Integer.parseInt(parts[coordinateIndex]);
            col = Integer.parseInt(parts[coordinateIndex + 1]);
        } catch (NumberFormatException e) {
            return new Command(Action.INVALID, null, "坐标必须是整数，例如：o 3 5");
        }

        if (row < 1 || row > rows || col < 1 || col > cols) {
            return new Command(Action.INVALID, null,
                    "坐标超出范围：行 1~" + rows + "，列 1~" + cols);
        }
        return new Command(action, new Position(row - 1, col - 1), null);
    }
}
