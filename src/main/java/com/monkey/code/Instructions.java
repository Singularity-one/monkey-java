package com.monkey.code;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * Instructions 代表字節碼指令序列
 */
public class Instructions {
    private final List<Byte> bytes;

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

    /**
     * 添加字節
     */
    public void add(byte b) {
        bytes.add(b);
    }

    /**
     * 添加多個字節
     */
    public void addAll(byte[] bs) {
        for (byte b : bs) {
            bytes.add(b);
        }
    }

    /**
     * 添加另一個 Instructions
     */
    public void addAll(Instructions other) {
        bytes.addAll(other.bytes);
    }

    /**
     * 獲取指定位置的字節
     */
    public byte get(int index) {
        return bytes.get(index);
    }

    /**
     * 設置指定位置的字節
     */
    public void set(int index, byte value) {
        bytes.set(index, value);
    }

    /**
     * 獲取長度
     */
    public int size() {
        return bytes.size();
    }

    /**
     * 轉換為字節陣列
     */
    public byte[] toByteArray() {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    /**
     * 轉換為字串表示（用於調試）
     */
    @Override
    public String toString() {
        StringBuilder out = new StringBuilder();
        int i = 0;

        while (i < bytes.size()) {
            Opcode op = Opcode.lookup(bytes.get(i));
            if (op == null) {
                out.append(String.format("ERROR: unknown opcode %d\n", bytes.get(i)));
                i++;
                continue;
            }

            int[] operandWidths = op.getOperandWidths();
            int[] operands = readOperands(operandWidths, i + 1);

            out.append(String.format("%04d %s", i, formatInstruction(op, operands)));
            out.append("\n");

            i += 1 + sumArray(operandWidths);
        }

        return out.toString();
    }

    /**
     * 格式化單條指令
     */
    private String formatInstruction(Opcode op, int[] operands) {
        int operandCount = op.getOperandWidths().length;

        if (operands.length != operandCount) {
            return String.format("ERROR: operand len %d does not match defined %d\n",
                    operands.length, operandCount);
        }

        switch (operandCount) {
            case 0:
                return op.getName();
            case 1:
                return String.format("%s %d", op.getName(), operands[0]);
            default:
                return String.format("ERROR: unhandled operandCount for %s\n", op.getName());
        }
    }

    /**
     * 讀取操作數
     */
    private int[] readOperands(int[] widths, int startPos) {
        int[] operands = new int[widths.length];
        int offset = 0;

        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];

            switch (width) {
                case 2:
                    operands[i] = readUint16(startPos + offset);
                    break;
            }

            offset += width;
        }

        return operands;
    }

    /**
     * 讀取 16 位無符號整數（大端序）
     */
    private int readUint16(int pos) {
        if (pos + 1 >= bytes.size()) {
            return 0;
        }
        return ((bytes.get(pos) & 0xFF) << 8) | (bytes.get(pos + 1) & 0xFF);
    }

    /**
     * 計算陣列總和
     */
    private int sumArray(int[] arr) {
        int sum = 0;
        for (int i : arr) {
            sum += i;
        }
        return sum;
    }

    public void clear() {
        bytes.clear();
    }
}
