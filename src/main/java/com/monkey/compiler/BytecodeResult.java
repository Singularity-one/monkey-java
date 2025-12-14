package com.monkey.compiler;
import com.monkey.code.Instructions;
import com.monkey.object.MonkeyObject;

import java.util.List;

/**
 * BytecodeResult 代表編譯結果
 */
public class BytecodeResult {
    private final Instructions instructions;
    private final List<MonkeyObject> constants;

    public BytecodeResult(Instructions instructions, List<MonkeyObject> constants) {
        this.instructions = instructions;
        this.constants = constants;
    }

    public Instructions getInstructions() {
        return instructions;
    }

    public List<MonkeyObject> getConstants() {
        return constants;
    }
}
