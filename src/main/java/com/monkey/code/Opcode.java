package com.monkey.code;

public enum Opcode {
    OP_CONSTANT((byte) 0),
    OP_ADD((byte) 1),
    OP_SUB((byte) 2),
    OP_MUL((byte) 3),
    OP_DIV((byte) 4),
    OP_POP((byte) 5),
    OP_TRUE((byte) 6),
    OP_FALSE((byte) 7),
    OP_EQUAL((byte) 8),
    OP_NOT_EQUAL((byte) 9),
    OP_GREATER_THAN((byte) 10),
    OP_MINUS((byte) 11),
    OP_BANG((byte) 12),
    OP_JUMP_NOT_TRUTHY((byte) 13),
    OP_JUMP((byte) 14),
    OP_NULL((byte) 15),
    OP_GET_GLOBAL((byte) 16),
    OP_SET_GLOBAL((byte) 17),
    OP_ARRAY((byte) 18),
    OP_HASH((byte) 19),
    OP_INDEX((byte) 20),

    // Chapter 7 - 函數相關操作碼
    OP_CALL((byte) 21),           // 調用函數 (操作數: 參數數量)
    OP_RETURN_VALUE((byte) 22),   // 返回值
    OP_RETURN((byte) 23),         // 返回 (無值)
    OP_GET_LOCAL((byte) 24),      // 獲取局部變量 (操作數: 局部索引)
    OP_SET_LOCAL((byte) 25),     // 設置局部變量 (操作數: 局部索引)

    // Chapter 8 - 內建函數
    OP_GET_BUILTIN((byte) 26);  // 獲取內建函數 (操作數: 內建函數索引)

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