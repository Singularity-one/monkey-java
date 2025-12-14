package com.monkey.code;

import java.util.HashMap;
import java.util.Map;

/**
 * Opcode 定義所有字節碼操作碼
 */
public enum Opcode {
    // 常量相關
    OpConstant((byte) 0, "OpConstant", new int[]{2}),  // 載入常量，2字節操作數

    // 算術運算
    OpAdd((byte) 1, "OpAdd", new int[]{}),
    OpSub((byte) 2, "OpSub", new int[]{}),
    OpMul((byte) 3, "OpMul", new int[]{}),
    OpDiv((byte) 4, "OpDiv", new int[]{}),

    // 布林值
    OpTrue((byte) 5, "OpTrue", new int[]{}),
    OpFalse((byte) 6, "OpFalse", new int[]{}),

    // 比較運算
    OpEqual((byte) 7, "OpEqual", new int[]{}),
    OpNotEqual((byte) 8, "OpNotEqual", new int[]{}),
    OpGreaterThan((byte) 9, "OpGreaterThan", new int[]{}),

    // 一元運算
    OpMinus((byte) 10, "OpMinus", new int[]{}),
    OpBang((byte) 11, "OpBang", new int[]{}),

    // 控制流
    OpJumpNotTruthy((byte) 12, "OpJumpNotTruthy", new int[]{2}),  // 條件跳轉
    OpJump((byte) 13, "OpJump", new int[]{2}),                     // 無條件跳轉

    // 空值
    OpNull((byte) 14, "OpNull", new int[]{}),

    // 堆疊操作
    OpPop((byte) 15, "OpPop", new int[]{}),

    // 資料結構（第二章新增）
    OpArray((byte) 16, "OpArray", new int[]{2}),      // 創建陣列，操作數：元素數量
    OpHash((byte) 17, "OpHash", new int[]{2}),        // 創建雜湊，操作數：元素數量（鍵值對*2）
    OpIndex((byte) 18, "OpIndex", new int[]{}),       // 索引訪問

    // 全局綁定（第二章新增）
    OpSetGlobal((byte) 19, "OpSetGlobal", new int[]{2}),  // 設置全局變數
    OpGetGlobal((byte) 20, "OpGetGlobal", new int[]{2});  // 獲取全局變數

    private final byte code;
    private final String name;
    private final int[] operandWidths;  // 每個操作數的字節寬度

    Opcode(byte code, String name, int[] operandWidths) {
        this.code = code;
        this.name = name;
        this.operandWidths = operandWidths;
    }

    public byte getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int[] getOperandWidths() {
        return operandWidths;
    }

    // 查找表：從字節碼值查找 Opcode
    private static final Map<Byte, Opcode> LOOKUP = new HashMap<>();

    static {
        for (Opcode op : Opcode.values()) {
            LOOKUP.put(op.code, op);
        }
    }

    /**
     * 從字節碼值查找 Opcode
     */
    public static Opcode lookup(byte code) {
        return LOOKUP.get(code);
    }
}
