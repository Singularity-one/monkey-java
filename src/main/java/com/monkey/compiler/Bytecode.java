package com.monkey.compiler;

import com.monkey.code.Instructions;
import com.monkey.object.MonkeyObject;

import java.util.List;

/**
 * Bytecode 表示編譯結果
 *
 * 編譯器將 AST 編譯為字節碼,字節碼包含兩個主要部分:
 * 1. Instructions - 字節碼指令序列
 * 2. Constants - 常量池
 *
 * 這兩部分會一起傳遞給 VM 執行
 *
 * Chapter 2: Hello Bytecode!
 */
public class Bytecode {
    private final Instructions instructions;    // 編譯生成的字節碼指令
    private final List<MonkeyObject> constants; // 常量池

    public Bytecode(Instructions instructions, List<MonkeyObject> constants) {
        this.instructions = instructions;
        this.constants = constants;
    }

    /**
     * 獲取字節碼指令
     */
    public Instructions getInstructions() {
        return instructions;
    }

    /**
     * 獲取常量池
     *
     * 常量池包含所有編譯時確定的常量:
     * - 整數字面量
     * - 布爾值
     * - 字符串(未來章節)
     * - 函數對象(未來章節)
     */
    public List<MonkeyObject> getConstants() {
        return constants;
    }

    @Override
    public String toString() {
        return String.format("Bytecode{\n  Instructions:\n%s\n  Constants: %d items\n}",
                instructions.toString(), constants.size());
    }
}
