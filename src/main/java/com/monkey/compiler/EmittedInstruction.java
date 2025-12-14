package com.monkey.compiler;
import com.monkey.code.Opcode;

/**
 * EmittedInstruction 記錄已發出的指令信息
 * 用於回填跳轉地址
 */
public class EmittedInstruction {
    private final Opcode opcode;
    private final int position;

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
