package com.monkey.vm;

import com.monkey.code.Instructions;
import com.monkey.object.CompiledFunctionObject;

/**
 * Frame 表示一個函數調用幀
 * Chapter 7: Functions
 *
 * 包含:
 * - fn: 被調用的函數
 * - ip: 指令指針 (當前執行位置)
 * - basePointer: 基指針 (指向函數在堆疊上的起始位置)
 */
public class Frame {
    private final CompiledFunctionObject fn;
    public int ip;
    public final int basePointer;

    public Frame(CompiledFunctionObject fn, int basePointer) {
        this.fn = fn;
        this.ip = -1;  // 初始化為 -1，第一次執行時會立即增加到 0
        this.basePointer = basePointer;
    }

    public Instructions instructions() {
        return fn.getInstructions();
    }
}