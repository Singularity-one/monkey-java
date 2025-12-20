package com.monkey.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instructions 表示字節碼指令序列
 * Chapter 6: String, Array and Hash (擴展)
 */
public class Instructions {
    private final List<Byte> bytes;

    // 操作碼定義映射
    private static final Map<Opcode, Definition> DEFINITIONS = new HashMap<>();

    static {
        // Chapter 2 指令
        DEFINITIONS.put(Opcode.OP_CONSTANT, new Definition("OpConstant", new int[]{2}));
        DEFINITIONS.put(Opcode.OP_ADD, new Definition("OpAdd", new int[]{}));
        DEFINITIONS.put(Opcode.OP_POP, new Definition("OpPop", new int[]{}));

        // Chapter 3 - 算術運算
        DEFINITIONS.put(Opcode.OP_SUB, new Definition("OpSub", new int[]{}));
        DEFINITIONS.put(Opcode.OP_MUL, new Definition("OpMul", new int[]{}));
        DEFINITIONS.put(Opcode.OP_DIV, new Definition("OpDiv", new int[]{}));

        // Chapter 3 - 布爾值
        DEFINITIONS.put(Opcode.OP_TRUE, new Definition("OpTrue", new int[]{}));
        DEFINITIONS.put(Opcode.OP_FALSE, new Definition("OpFalse", new int[]{}));

        // Chapter 3 - 比較運算
        DEFINITIONS.put(Opcode.OP_EQUAL, new Definition("OpEqual", new int[]{}));
        DEFINITIONS.put(Opcode.OP_NOT_EQUAL, new Definition("OpNotEqual", new int[]{}));
        DEFINITIONS.put(Opcode.OP_GREATER_THAN, new Definition("OpGreaterThan", new int[]{}));

        // Chapter 3 - 前綴運算
        DEFINITIONS.put(Opcode.OP_MINUS, new Definition("OpMinus", new int[]{}));
        DEFINITIONS.put(Opcode.OP_BANG, new Definition("OpBang", new int[]{}));

        // Chapter 4 - 跳轉指令
        DEFINITIONS.put(Opcode.OP_JUMP_NOT_TRUTHY, new Definition("OpJumpNotTruthy", new int[]{2}));
        DEFINITIONS.put(Opcode.OP_JUMP, new Definition("OpJump", new int[]{2}));

        // Chapter 4 - Null 值
        DEFINITIONS.put(Opcode.OP_NULL, new Definition("OpNull", new int[]{}));

        // Chapter 5 - 全局變量 (2 字節操作數: 全局變量索引)
        DEFINITIONS.put(Opcode.OP_SET_GLOBAL, new Definition("OpSetGlobal", new int[]{2}));
        DEFINITIONS.put(Opcode.OP_GET_GLOBAL, new Definition("OpGetGlobal", new int[]{2}));

        // Chapter 6 - 複合數據類型（新增）
        DEFINITIONS.put(Opcode.OP_ARRAY, new Definition("OpArray", new int[]{2}));
        DEFINITIONS.put(Opcode.OP_HASH, new Definition("OpHash", new int[]{2}));
        DEFINITIONS.put(Opcode.OP_INDEX, new Definition("OpIndex", new int[]{}));
    }

    public Instructions() {
        this.bytes = new ArrayList<>();
    }

    public Instructions(List<Byte> bytes) {
        this.bytes = new ArrayList<>(bytes);
    }

    public Instructions(byte[] bytes) {
        this.bytes = new ArrayList<>();
        for (byte b : bytes) {
            this.bytes.add(b);
        }
    }

    public static Definition lookup(byte op) {
        Opcode opcode = Opcode.fromByte(op);
        Definition def = DEFINITIONS.get(opcode);
        if (def == null) {
            throw new IllegalArgumentException("Opcode " + op + " undefined");
        }
        return def;
    }

    public static byte[] make(Opcode op, int... operands) {
        Definition def = DEFINITIONS.get(op);
        if (def == null) {
            return new byte[]{};
        }

        int instructionLen = 1;
        for (int w : def.getOperandWidths()) {
            instructionLen += w;
        }

        byte[] instruction = new byte[instructionLen];
        instruction[0] = op.getValue();

        int offset = 1;
        for (int i = 0; i < operands.length; i++) {
            int width = def.getOperandWidths()[i];
            switch (width) {
                case 2:
                    putUint16(instruction, offset, operands[i]);
                    break;
                case 1:
                    instruction[offset] = (byte) (operands[i] & 0xFF);
                    break;
            }
            offset += width;
        }

        return instruction;
    }

    public static int readUint16(byte[] ins) {
        return ((ins[0] & 0xFF) << 8) | (ins[1] & 0xFF);
    }

    public static ReadOperandsResult readOperands(Definition def, byte[] ins) {
        int[] operands = new int[def.getOperandWidths().length];
        int offset = 0;

        for (int i = 0; i < def.getOperandWidths().length; i++) {
            int width = def.getOperandWidths()[i];
            switch (width) {
                case 2:
                    byte[] slice = new byte[2];
                    System.arraycopy(ins, offset, slice, 0, 2);
                    operands[i] = readUint16(slice);
                    break;
                case 1:
                    operands[i] = ins[offset] & 0xFF;
                    break;
            }
            offset += width;
        }

        return new ReadOperandsResult(operands, offset);
    }

    private static void putUint16(byte[] arr, int offset, int value) {
        arr[offset] = (byte) ((value >> 8) & 0xFF);
        arr[offset + 1] = (byte) (value & 0xFF);
    }

    public void append(byte[] ins) {
        for (byte b : ins) {
            bytes.add(b);
        }
    }

    public void replaceInstruction(int pos, byte[] newInstruction) {
        for (int i = 0; i < newInstruction.length; i++) {
            bytes.set(pos + i, newInstruction[i]);
        }
    }

    public void changeOperand(int opPos, int operand) {
        Opcode op = Opcode.fromByte(bytes.get(opPos));
        byte[] newInstruction = make(op, operand);
        replaceInstruction(opPos, newInstruction);
    }

    public int size() {
        return bytes.size();
    }

    public byte get(int index) {
        return bytes.get(index);
    }

    public void removeLast(int n) {
        for (int i = 0; i < n; i++) {
            if (!bytes.isEmpty()) {
                bytes.remove(bytes.size() - 1);
            }
        }
    }

    public byte[] toByteArray() {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        int i = 0;

        while (i < bytes.size()) {
            Definition def;
            try {
                def = lookup(bytes.get(i));
            } catch (IllegalArgumentException e) {
                out.append(String.format("ERROR: %s\n", e.getMessage()));
                i++;
                continue;
            }

            byte[] operandBytes = new byte[bytes.size() - i - 1];
            for (int j = 0; j < operandBytes.length; j++) {
                operandBytes[j] = bytes.get(i + 1 + j);
            }

            ReadOperandsResult result = readOperands(def, operandBytes);
            out.append(String.format("%04d %s\n", i, fmtInstruction(def, result.operands)));

            i += 1 + result.bytesRead;
        }

        return out.toString();
    }

    private String fmtInstruction(Definition def, int[] operands) {
        int operandCount = def.getOperandWidths().length;

        if (operands.length != operandCount) {
            return String.format("ERROR: operand len %d does not match defined %d\n",
                    operands.length, operandCount);
        }

        switch (operandCount) {
            case 0:
                return def.getName();
            case 1:
                return String.format("%s %d", def.getName(), operands[0]);
            case 2:
                return String.format("%s %d %d", def.getName(), operands[0], operands[1]);
        }

        return String.format("ERROR: unhandled operandCount for %s\n", def.getName());
    }

    public static class ReadOperandsResult {
        public final int[] operands;
        public final int bytesRead;

        public ReadOperandsResult(int[] operands, int bytesRead) {
            this.operands = operands;
            this.bytesRead = bytesRead;
        }
    }

    public static class Definition {
        private final String name;
        private final int[] operandWidths;

        public Definition(String name, int[] operandWidths) {
            this.name = name;
            this.operandWidths = operandWidths;
        }

        public String getName() {
            return name;
        }

        public int[] getOperandWidths() {
            return operandWidths;
        }
    }
}