package com.liziyan.minesweeper.model;

/**
 * 难度配置。任务书要求支持初级、中级、高级三种难度，这里用枚举集中管理，避免散落的魔法数字。
 */
public enum Difficulty {
    BEGINNER("初级", 9, 9, 10),
    INTERMEDIATE("中级", 12, 12, 20),
    EXPERT("高级", 16, 16, 40);

    private final String label;
    private final int rows;
    private final int cols;
    private final int mines;

    Difficulty(String label, int rows, int cols, int mines) {
        this.label = label;
        this.rows = rows;
        this.cols = cols;
        this.mines = mines;
    }

    public String label() {
        return label;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    public int mines() {
        return mines;
    }

    public GameConfig toConfig() {
        return new GameConfig(rows, cols, mines);
    }

    /** 菜单里展示的说明文字，例如「初级 9×9，10 雷」。 */
    public String description() {
        return label + " " + rows + "×" + cols + "，" + mines + " 雷";
    }

    /**
     * 根据菜单输入解析难度。
     *
     * @return 解析不出时返回 {@code null}，由调用方提示用户重新输入
     */
    public static Difficulty fromMenu(String text) {
        if (text == null) {
            return null;
        }
        return switch (text.trim()) {
            case "1" -> BEGINNER;
            case "2" -> INTERMEDIATE;
            case "3" -> EXPERT;
            default -> null;
        };
    }
}
