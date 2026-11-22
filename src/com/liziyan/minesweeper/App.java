package com.liziyan.minesweeper;

import com.liziyan.minesweeper.model.Difficulty;
import com.liziyan.minesweeper.model.GameConfig;
import com.liziyan.minesweeper.model.Position;
import com.liziyan.minesweeper.service.GameService;
import com.liziyan.minesweeper.service.MoveResult;
import com.liziyan.minesweeper.util.CommandParser;
import com.liziyan.minesweeper.util.ConsoleInput;
import com.liziyan.minesweeper.view.ConsoleView;

import java.util.Random;

/**
 * 程序入口：只负责菜单流程和调度。
 * <p>游戏规则在 {@code service} 包，界面在 {@code view} 包，输入解析在 {@code util} 包，
 * 保证 main 方法只做入口，不堆业务代码。</p>
 */
public final class App {
    private final ConsoleView view = new ConsoleView();
    private final ConsoleInput input = new ConsoleInput();
    private final Random random = new Random();

    public static void main(String[] args) {
        new App().run();
    }

    private void run() {
        view.printBanner();
        try {
            boolean running = true;
            while (running) {
                view.printMenu();
                String choice = input.readLine("请选择 > ");
                if (choice == null) {
                    view.info("没有读到输入，游戏退出。");
                    return;
                }
                choice = choice.trim();
                if (choice.equalsIgnoreCase("q") || choice.equalsIgnoreCase("quit")) {
                    running = false;
                    continue;
                }
                GameConfig config = readConfig(choice);
                if (config != null) {
                    play(config);
                }
            }
        } finally {
            input.close();
        }
        view.info("游戏结束，再见！");
    }

    /**
     * 解析难度选择或自定义棋盘。
     *
     * @return 配置合法时返回配置；否则打印友好提示并返回 {@code null}
     */
    private GameConfig readConfig(String choice) {
        Difficulty difficulty = Difficulty.fromMenu(choice);
        if (difficulty != null) {
            return difficulty.toConfig();
        }

        String[] parts = choice.split("\\s+");
        if (parts.length != 3) {
            view.error("输入无效：请输入 1/2/3，或 \"行 列 雷数\"，或 q 退出");
            return null;
        }
        try {
            int rows = Integer.parseInt(parts[0]);
            int cols = Integer.parseInt(parts[1]);
            int mines = Integer.parseInt(parts[2]);
            return new GameConfig(rows, cols, mines);
        } catch (NumberFormatException e) {
            view.error("输入无效：行、列、雷数都必须是整数");
        } catch (IllegalArgumentException e) {
            view.error("无法开始游戏：" + e.getMessage());
        }
        return null;
    }

    /** 完整进行一局游戏，直到胜利、失败或玩家主动退出。 */
    private void play(GameConfig config) {
        GameService game = new GameService(config, random);
        view.printHelp();
        view.printBoard(game);

        while (!game.isFinished()) {
            String line = input.readLine("> ");
            if (line == null) {
                return;
            }
            CommandParser.Command command = CommandParser.parse(line, config.rows(), config.cols());
            if (command.isQuit()) {
                view.info("已退出本局。");
                return;
            }
            if (command.isInvalid()) {
                view.error(command.error());
                continue;
            }
            apply(game, command);
        }

        view.printBoard(game);
        view.printResult(game);
        input.readLine("按回车返回主菜单...");
    }

    private void apply(GameService game, CommandParser.Command command) {
        Position position = command.position();
        MoveResult result = command.action() == CommandParser.Action.OPEN
                ? game.open(position)
                : game.toggleFlag(position);

        if (result == MoveResult.OK) {
            view.printBoard(game);
        } else if (result != MoveResult.HIT_MINE && result != MoveResult.WON) {
            view.error(result.message());
        }
    }
}
