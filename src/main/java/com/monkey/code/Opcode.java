package com.monkey.code;

/**
 * Opcode 表示字節碼操作碼
 * Chapter 5: Keeping Track of Names
 *
 * 新增:
 * - 全局變量指令 (SetGlobal, GetGlobal)
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
    OP_BANG((byte) 12),         // 邏輯非 (!x)

    // Chapter 4 - 跳轉指令
    OP_JUMP_NOT_TRUTHY((byte) 13), // 條件跳轉
    OP_JUMP((byte) 14),            // 無條件跳轉

    // Chapter 4 - Null 值
    OP_NULL((byte) 15),         // 推入 null

    // Chapter 5 - 全局變量
    OP_SET_GLOBAL((byte) 16),   // 設置全局變量
    OP_GET_GLOBAL((byte) 17);   // 獲取全局變量

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