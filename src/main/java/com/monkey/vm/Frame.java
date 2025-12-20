package com.monkey.vm;

import com.monkey.code.Instructions;
import com.monkey.object.ClosureObject;

/**
 * Frame 表示一個函數調用幀
 * Chapter 9: Closures (擴展)
 */
public class Frame {
    private final ClosureObject cl;  // Chapter 9: 改為存儲閉包而不是函數
    public int ip;
    public final int basePointer;

    public Frame(ClosureObject cl, int basePointer) {
        this.cl = cl;
        this.ip = -1;
        this.basePointer = basePointer;
    }

    public Instructions instructions() {
        return cl.getFn().getInstructions();
    }

    /**
     * Chapter 9: 獲取閉包
     */
    public ClosureObject getClosure() {
        return cl;
    }
}