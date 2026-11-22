package com.liziyan.minesweeper.util;

/**
 * 终端 ANSI 颜色常量。
 * <p>IDEA 运行窗口和 Windows Terminal 都支持 ANSI；如果终端不支持，可以设置环境变量
 * {@code NO_COLOR=1} 关闭颜色，避免出现乱码。</p>
 */
public final class AnsiColor {
    private static final boolean SUPPORTED = System.getenv("NO_COLOR") == null;

    public static final String RESET = SUPPORTED ? "\u001B[0m" : "";
    public static final String BOLD = SUPPORTED ? "\u001B[1m" : "";
    public static final String RED = SUPPORTED ? "\u001B[31m" : "";
    public static final String GREEN = SUPPORTED ? "\u001B[32m" : "";
    public static final String YELLOW = SUPPORTED ? "\u001B[33m" : "";
    public static final String BLUE = SUPPORTED ? "\u001B[34m" : "";
    public static final String PURPLE = SUPPORTED ? "\u001B[35m" : "";
    public static final String CYAN = SUPPORTED ? "\u001B[36m" : "";

    private AnsiColor() {
    }
}
