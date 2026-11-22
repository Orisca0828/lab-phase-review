package com.liziyan.minesweeper.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * 控制台输入封装。
 * <p>统一处理提示语和读行异常：读不到内容（例如输入流结束）时返回 {@code null}，
 * 由调用方按「退出」处理，避免空指针异常。</p>
 */
public final class ConsoleInput implements AutoCloseable {
    private final BufferedReader reader;

    public ConsoleInput() {
        this.reader = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * 打印提示语并读取一行。
     *
     * @return 读取到的内容；输入流结束或发生 IO 异常时返回 {@code null}
     */
    public String readLine(String prompt) {
        System.out.print(prompt);
        System.out.flush();
        try {
            return reader.readLine();
        } catch (IOException e) {
            return null;
        }
    }

    @Override
    public void close() {
        try {
            reader.close();
        } catch (IOException ignored) {
            // 关闭失败不影响程序退出
        }
    }
}
