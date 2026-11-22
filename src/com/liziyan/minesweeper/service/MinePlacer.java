package com.liziyan.minesweeper.service;

import com.liziyan.minesweeper.model.Board;
import com.liziyan.minesweeper.model.Position;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;

/**
 * 布雷器：负责「首次翻开之后才布雷，并保证首点及其周围 3×3 安全」。
 * <p>把随机算法从游戏流程里拆出来，方便替换布雷策略（例如固定种子用于测试）。</p>
 */
public final class MinePlacer {
    private final Random random;

    public MinePlacer(Random random) {
        this.random = Objects.requireNonNull(random, "random");
    }

    public void place(Board board, Position firstClick) {
        List<Position> candidates = collectCandidates(board, firstClick);
        Collections.shuffle(candidates, random);
        for (int i = 0; i < board.mineCount(); i++) {
            board.setMine(candidates.get(i), true);
        }
        computeAdjacentMines(board);
    }

    private List<Position> collectCandidates(Board board, Position firstClick) {
        List<Position> candidates = new ArrayList<>();
        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                if (Math.abs(r - firstClick.row()) <= 1 && Math.abs(c - firstClick.col()) <= 1) {
                    continue;
                }
                candidates.add(new Position(r, c));
            }
        }
        // 棋盘太小、去掉 3×3 后放不下所有雷时，退化为只保证首点本身不是雷
        if (candidates.size() < board.mineCount()) {
            candidates.clear();
            for (int r = 0; r < board.rows(); r++) {
                for (int c = 0; c < board.cols(); c++) {
                    Position position = new Position(r, c);
                    if (!position.equals(firstClick)) {
                        candidates.add(position);
                    }
                }
            }
        }
        return candidates;
    }

    private void computeAdjacentMines(Board board) {
        for (int r = 0; r < board.rows(); r++) {
            for (int c = 0; c < board.cols(); c++) {
                Position position = new Position(r, c);
                if (board.isMine(position)) {
                    continue;
                }
                int count = 0;
                for (Position neighbour : board.neighbours(position)) {
                    if (board.isMine(neighbour)) {
                        count++;
                    }
                }
                board.setAdjacentMines(position, count);
            }
        }
    }
}
