package com.monkey.compiler;

import com.monkey.code.Opcode;

/**
 * EmittedInstruction 表示已發射的指令
 * Chapter 7: Functions (擴展)
 */
public class EmittedInstruction {
    private Opcode opcode;
    private final int position;

    public EmittedInstruction(Opcode opcode, int position) {
        this.opcode = opcode;
        this.position = position;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    /**
     * Chapter 7: 設置操作碼 (用於替換 OpPop 為 OpReturnValue)
     */
    public void setOpcode(Opcode opcode) {
        this.opcode = opcode;
    }

    public int getPosition() {
        return position;
    }
}