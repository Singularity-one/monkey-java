package com.monkey.compiler;

import com.monkey.code.Instructions;
import com.monkey.code.Opcode;

/**
 * CompilationScope 表示編譯作用域
 * Chapter 7: Functions
 *
 * 每個作用域包含:
 * - instructions: 當前作用域的指令序列
 * - lastInstruction: 最後發射的指令
 * - previousInstruction: 倒數第二條發射的指令
 */
public class CompilationScope {
    private Instructions instructions;
    private EmittedInstruction lastInstruction;
    private EmittedInstruction previousInstruction;

    public CompilationScope() {
        this.instructions = new Instructions();
    }

    public Instructions getInstructions() {
        return instructions;
    }

    public void setInstructions(Instructions instructions) {
        this.instructions = instructions;
    }

    public EmittedInstruction getLastInstruction() {
        return lastInstruction;
    }

    public void setLastInstruction(EmittedInstruction lastInstruction) {
        this.lastInstruction = lastInstruction;
    }

    public EmittedInstruction getPreviousInstruction() {
        return previousInstruction;
    }

    public void setPreviousInstruction(EmittedInstruction previousInstruction) {
        this.previousInstruction = previousInstruction;
    }
}