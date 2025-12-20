package com.monkey.object;

import com.monkey.code.Instructions;

/**
 * CompiledFunctionObject 表示編譯後的函數
 * Chapter 7: Functions
 *
 * 包含:
 * - instructions: 函數的字節碼指令
 * - numLocals: 局部變量數量
 * - numParameters: 參數數量
 */
public class CompiledFunctionObject implements MonkeyObject {
    private final Instructions instructions;
    private final int numLocals;
    private final int numParameters;

    public CompiledFunctionObject(Instructions instructions) {
        this(instructions, 0, 0);
    }

    public CompiledFunctionObject(Instructions instructions, int numLocals, int numParameters) {
        this.instructions = instructions;
        this.numLocals = numLocals;
        this.numParameters = numParameters;
    }

    @Override
    public ObjectType type() {
        return ObjectType.COMPILED_FUNCTION;
    }

    @Override
    public String inspect() {
        return String.format("CompiledFunction[%s]", Integer.toHexString(hashCode()));
    }

    public Instructions getInstructions() {
        return instructions;
    }

    public int getNumLocals() {
        return numLocals;
    }

    public int getNumParameters() {
        return numParameters;
    }

    @Override
    public HashKey hashKey() {
        return null; // 函數不可雜湊
    }
}