package com.liziyan.minesweeper.view;

import com.liziyan.minesweeper.model.Board;
import com.liziyan.minesweeper.model.Cell;
import com.liziyan.minesweeper.model.Difficulty;
import com.liziyan.minesweeper.model.GameState;
import com.liziyan.minesweeper.service.GameService;
import com.liziyan.minesweeper.util.AnsiColor;

/**
 * 控制台界面：只负责「打印」和「提示」，不参与任何游戏规则判断。
 */
public final class ConsoleView {

    public void printBanner() {
        System.out.println();
        System.out.println(AnsiColor.CYAN + "========================================" + AnsiColor.RESET);
        System.out.println(AnsiColor.BOLD + "          控制台扫雷 Minesweeper" + AnsiColor.RESET);
        System.out.println(AnsiColor.CYAN + "========================================" + AnsiColor.RESET);
    }

    public void printMenu() {
        System.out.println();
        System.out.println("请选择难度：");
        for (Difficulty difficulty : Difficulty.values()) {
            System.out.printf("  %d. %s%n", difficulty.ordinal() + 1, difficulty.description());
        }
        System.out.println("  也可以直接输入：行 列 雷数（例如：10 10 15）");
        System.out.println("  输入 q 或 quit 退出游戏");
    }

    public void printHelp() {
        System.out.println();
        System.out.println("操作说明：");
        System.out.println("  翻开格子：o 行 列（也可直接输入 行 列）");
        System.out.println("  插旗标记：f 行 列（再次输入取消插旗）");
        System.out.println("  退出本局：q");
        System.out.println("坐标从 1 开始。");
    }

    /** 渲染棋盘：行号、列号对齐，插旗用 F，地雷用 *。 */
    public void printBoard(GameService game) {
        Board board = game.board();

        System.out.println();
        System.out.print("    ");
        for (int col = 1; col <= board.cols(); col++) {
            System.out.printf("%3d", col);
        }
        System.out.println();

        printBorder(board.cols());

        for (int row = 0; row < board.rows(); row++) {
            System.out.printf("%2d |", row + 1);
            for (int col = 0; col < board.cols(); col++) {
                System.out.print(cellText(board.cellAt(row, col)));
            }
            System.out.println("|");
        }

        printBorder(board.cols());

        long seconds = game.elapsedSeconds();
        System.out.printf("  剩余雷数: %2d | 已翻开: %2d/%2d | 耗时: %02d:%02d%n",
                game.remainingMines(),
                game.revealedSafeCells(),
                game.safeCells(),
                seconds / 60,
                seconds % 60);
    }

    private void printBorder(int cols) {
        System.out.print("   +");
        System.out.print("-".repeat(cols * 3));
        System.out.println("+");
    }

    private String cellText(Cell cell) {
        if (cell.isFlagged() && !cell.isRevealed()) {
            return AnsiColor.RED + "  F" + AnsiColor.RESET;
        }
        if (!cell.isRevealed()) {
            return "  #";
        }
        if (cell.isMine()) {
            return AnsiColor.BOLD + AnsiColor.RED + "  *" + AnsiColor.RESET;
        }
        if (cell.adjacentMines() == 0) {
            return "   ";
        }
        return colorOf(cell.adjacentMines()) + "  " + cell.adjacentMines() + AnsiColor.RESET;
    }

    private String colorOf(int adjacentMines) {
        return switch (adjacentMines) {
            case 1 -> AnsiColor.BLUE;
            case 2 -> AnsiColor.GREEN;
            case 3 -> AnsiColor.RED;
            case 4 -> AnsiColor.PURPLE;
            default -> AnsiColor.CYAN;
        };
    }

    public void printResult(GameService game) {
        long seconds = game.elapsedSeconds();
        if (game.state() == GameState.WON) {
            System.out.println(AnsiColor.GREEN + "🎉 恭喜，排雷成功！用时 "
                    + formatDuration(seconds) + AnsiColor.RESET);
        } else if (game.state() == GameState.LOST) {
            System.out.println(AnsiColor.RED + "💥 踩到地雷，游戏结束。本局用时 "
                    + formatDuration(seconds) + AnsiColor.RESET);
        }
    }

    private String formatDuration(long seconds) {
        return String.format("%02d 分 %02d 秒", seconds / 60, seconds % 60);
    }

    public void info(String message) {
        System.out.println(message);
    }

    public void error(String message) {
        System.out.println(AnsiColor.RED + "❌ " + message + AnsiColor.RESET);
    }
}
