package com.monkey.code;

import java.util.HashMap;
import java.util.Map;

/**
 * Opcode 表示字節碼操作碼
 * Chapter 2: Hello Bytecode!
 *
 * 操作碼是字節碼指令的第一個字節,用於標識指令的類型
 */
public enum Opcode {
    // Chapter 2 - 基礎指令
    OP_CONSTANT((byte) 0),  // 載入常量到堆疊
    OP_ADD((byte) 1),       // 加法運算
    OP_POP((byte) 2),       // 彈出堆疊頂部元素

    // Chapter 3 預留 - 更多算術運算
    OP_SUB((byte) 3),       // 減法運算
    OP_MUL((byte) 4),       // 乘法運算
    OP_DIV((byte) 5);       // 除法運算

    private final byte value;

    Opcode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * 從字節值轉換為 Opcode
     * 用於 VM 的取指階段
     */
    public static Opcode fromByte(byte b) {
        for (Opcode op : values()) {
            if (op.value == b) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown opcode: " + b);
    }
}