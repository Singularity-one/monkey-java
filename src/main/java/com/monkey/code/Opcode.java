package com.monkey.code;

/**
 * Opcode 表示字節碼操作碼
 * Chapter 6: String, Array and Hash
 */
public enum Opcode {
    // Chapter 2 - 基礎指令
    OP_CONSTANT((byte) 0),
    OP_ADD((byte) 1),
    OP_POP((byte) 2),

    // Chapter 3 - 算術運算
    OP_SUB((byte) 3),
    OP_MUL((byte) 4),
    OP_DIV((byte) 5),

    // Chapter 3 - 布爾值
    OP_TRUE((byte) 6),
    OP_FALSE((byte) 7),

    // Chapter 3 - 比較運算
    OP_EQUAL((byte) 8),
    OP_NOT_EQUAL((byte) 9),
    OP_GREATER_THAN((byte) 10),

    // Chapter 3 - 前綴運算
    OP_MINUS((byte) 11),
    OP_BANG((byte) 12),

    // Chapter 4 - 跳轉指令
    OP_JUMP_NOT_TRUTHY((byte) 13),
    OP_JUMP((byte) 14),

    // Chapter 4 - Null 值
    OP_NULL((byte) 15),

    // Chapter 5 - 全局變量
    OP_SET_GLOBAL((byte) 16),
    OP_GET_GLOBAL((byte) 17),

    // Chapter 6 - 複合數據類型（新增）
    OP_ARRAY((byte) 18),        // 構建陣列
    OP_HASH((byte) 19),         // 構建雜湊表
    OP_INDEX((byte) 20);        // 索引運算

    private final byte value;

    Opcode(byte value) {
        this.value = value;
    }

    public byte getValue() {
        return value;
    }

    public static Opcode fromByte(byte b) {
        for (Opcode op : values()) {
            if (op.value == b) {
                return op;
            }
        }
        throw new IllegalArgumentException("Unknown opcode: " + b);
    }
}