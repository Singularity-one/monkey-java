package com.monkey.code;

import java.util.HashMap;
import java.util.Map;

/**
 * Opcode 表示字節碼操作碼
 * Chapter 3: Compiling Expressions
 *
 * 新增:
 * - 更多算術運算 (Sub, Mul, Div)
 * - 布爾值 (True, False)
 * - 比較運算 (Equal, NotEqual, GreaterThan)
 * - 前綴運算 (Minus, Bang)
 * - 堆疊管理 (Pop)
 */
public enum Opcode {
    // Chapter 2 - 基礎指令
    OP_CONSTANT((byte) 0),      // 載入常量到堆疊
    OP_ADD((byte) 1),           // 加法運算
    OP_POP((byte) 2),           // 彈出堆疊頂部元素

    // Chapter 3 - 算術運算
    OP_SUB((byte) 3),           // 減法運算
    OP_MUL((byte) 4),           // 乘法運算
    OP_DIV((byte) 5),           // 除法運算

    // Chapter 3 - 布爾值
    OP_TRUE((byte) 6),          // 推入 true
    OP_FALSE((byte) 7),         // 推入 false

    // Chapter 3 - 比較運算
    OP_EQUAL((byte) 8),         // 相等比較 (==)
    OP_NOT_EQUAL((byte) 9),     // 不等比較 (!=)
    OP_GREATER_THAN((byte) 10), // 大於比較 (>)

    // Chapter 3 - 前綴運算
    OP_MINUS((byte) 11),        // 取負 (-x)
    OP_BANG((byte) 12);         // 邏輯非 (!x)

    private final byte value;

    Opcode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    /**
     * 從字節值轉換為 Opcode
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