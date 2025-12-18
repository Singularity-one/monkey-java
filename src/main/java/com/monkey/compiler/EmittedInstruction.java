package com.monkey.compiler;
import com.monkey.code.Opcode;

import com.monkey.code.Opcode;

/**
 * EmittedInstruction 記錄發射的指令信息
 * Chapter 4: Conditionals
 *
 * 用於追蹤最後發射的指令,以便進行回填等操作
 */
public class EmittedInstruction {
    private final Opcode opcode;    // 操作碼
    private final int position;      // 指令在字節碼中的位置

    public EmittedInstruction(Opcode opcode, int position) {
        this.opcode = opcode;
        this.position = position;
    }

    public Opcode getOpcode() {
        return opcode;
    }

    public int getPosition() {
        return position;
    }
}
