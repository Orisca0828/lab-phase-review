package com.liziyan.minesweeper.service;

import com.liziyan.minesweeper.model.Board;
import com.liziyan.minesweeper.model.Cell;
import com.liziyan.minesweeper.model.GameConfig;
import com.liziyan.minesweeper.model.GameState;
import com.liziyan.minesweeper.model.Position;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;

/**
 * 游戏核心业务：首次布雷、翻开、空白级联展开、插旗、胜负判定和计时。
 * <p>本类不直接读写控制台，只通过返回值把结果告诉界面层，便于以后换成图形界面。</p>
 */
public final class GameService {
    private final GameConfig config;
    private final Board board;
    private final MinePlacer minePlacer;

    private GameState state = GameState.PLAYING;
    private boolean minesPlaced;
    private int revealedSafeCells;
    private int flagsUsed;
    private final long startTime;
    private long endTime;

    public GameService(GameConfig config) {
        this(config, new Random());
    }

    public GameService(GameConfig config, Random random) {
        if (config == null) {
            throw new IllegalArgumentException("游戏配置不能为空");
        }
        this.config = config;
        this.board = new Board(config.rows(), config.cols(), config.mines());
        this.minePlacer = new MinePlacer(random);
        this.startTime = System.currentTimeMillis();
    }

    public Board board() {
        return board;
    }

    public GameConfig config() {
        return config;
    }

    public GameState state() {
        return state;
    }

    public boolean isFinished() {
        return state.isFinished();
    }

    public int flagsUsed() {
        return flagsUsed;
    }

    /** 剩余雷数 = 总雷数 - 已插旗数（只用于展示，不参与胜负判定）。 */
    public int remainingMines() {
        return board.mineCount() - flagsUsed;
    }

    public int revealedSafeCells() {
        return revealedSafeCells;
    }

    public int safeCells() {
        return config.safeCells();
    }

    public long elapsedSeconds() {
        long end = state.isFinished() ? endTime : System.currentTimeMillis();
        return Math.max(0, (end - startTime) / 1000);
    }

    /**
     * 翻开一个格子。首次翻开时先布雷，保证首点及其 3×3 范围内没有地雷。
     */
    public MoveResult open(Position position) {
        if (state.isFinished()) {
            return MoveResult.GAME_FINISHED;
        }
        if (position == null || !board.inBounds(position)) {
            return MoveResult.OUT_OF_BOUNDS;
        }
        Cell cell = board.cellAt(position);
        if (cell.isRevealed()) {
            return MoveResult.ALREADY_REVEALED;
        }
        if (cell.isFlagged()) {
            return MoveResult.FLAGGED;
        }

        if (!minesPlaced) {
            minePlacer.place(board, position);
            minesPlaced = true;
        }

        if (cell.isMine()) {
            board.reveal(position);
            board.revealAllMines();
            finish(GameState.LOST);
            return MoveResult.HIT_MINE;
        }

        cascadeReveal(position);
        if (revealedSafeCells == config.safeCells()) {
            finish(GameState.WON);
            return MoveResult.WON;
        }
        return MoveResult.OK;
    }

    /** 插旗 / 取消插旗。已翻开的格子不能插旗，旗子数量不能超过雷数。 */
    public MoveResult toggleFlag(Position position) {
        if (state.isFinished()) {
            return MoveResult.GAME_FINISHED;
        }
        if (position == null || !board.inBounds(position)) {
            return MoveResult.OUT_OF_BOUNDS;
        }
        Cell cell = board.cellAt(position);
        if (cell.isRevealed()) {
            return MoveResult.FLAG_ON_REVEALED;
        }
        if (cell.isFlagged()) {
            board.flag(position, false);
            flagsUsed--;
            return MoveResult.OK;
        }
        if (flagsUsed >= board.mineCount()) {
            return MoveResult.FLAG_LIMIT;
        }
        board.flag(position, true);
        flagsUsed++;
        return MoveResult.OK;
    }

    /**
     * 空白级联展开：使用 BFS（队列）把周围 8 格中「没有雷且周围雷数为 0」的格子连续翻开，
     * 遇到数字格停止扩散，避免递归过深导致栈溢出。
     */
    private void cascadeReveal(Position start) {
        Deque<Position> queue = new ArrayDeque<>();
        queue.addLast(start);

        while (!queue.isEmpty()) {
            Position current = queue.pollFirst();
            Cell cell = board.cellAt(current);
            if (cell.isRevealed() || cell.isFlagged() || cell.isMine()) {
                continue;
            }
            board.reveal(current);
            revealedSafeCells++;

            if (cell.adjacentMines() == 0) {
                for (Position neighbour : board.neighbours(current)) {
                    Cell next = board.cellAt(neighbour);
                    if (!next.isRevealed() && !next.isFlagged() && !next.isMine()) {
                        queue.addLast(neighbour);
                    }
                }
            }
        }
    }

    private void finish(GameState finalState) {
        this.state = finalState;
        this.endTime = System.currentTimeMillis();
    }
}
