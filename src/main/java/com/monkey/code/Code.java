package com.monkey.code;
import java.nio.ByteBuffer;

/**
 * Code 提供字節碼生成的工具方法
 */
public class Code {

    /**
     * 生成指令字節碼
     *
     * @param op 操作碼
     * @param operands 操作數
     * @return 字節碼
     */
    public static byte[] make(Opcode op, int... operands) {
        int[] widths = op.getOperandWidths();

        // 計算總長度：1 字節操作碼 + 操作數字節
        int instructionLen = 1;
        for (int w : widths) {
            instructionLen += w;
        }

        byte[] instruction = new byte[instructionLen];
        instruction[0] = op.getCode();

        int offset = 1;
        for (int i = 0; i < operands.length; i++) {
            int width = widths[i];
            switch (width) {
                case 2:
                    putUint16(instruction, offset, operands[i]);
                    break;
            }
            offset += width;
        }

        return instruction;
    }

    /**
     * 寫入 16 位無符號整數（大端序）
     */
    private static void putUint16(byte[] bytes, int offset, int value) {
        bytes[offset] = (byte) ((value >> 8) & 0xFF);
        bytes[offset + 1] = (byte) (value & 0xFF);
    }

    /**
     * 讀取 16 位無符號整數（大端序）
     */
    public static int readUint16(byte[] bytes, int offset) {
        return ((bytes[offset] & 0xFF) << 8) | (bytes[offset + 1] & 0xFF);
    }

    /**
     * 讀取操作數
     */
    public static ReadOperandsResult readOperands(Opcode op, byte[] ins, int startPos) {
        int[] widths = op.getOperandWidths();
        int[] operands = new int[widths.length];
        int offset = 0;

        for (int i = 0; i < widths.length; i++) {
            int width = widths[i];

            switch (width) {
                case 2:
                    operands[i] = readUint16(ins, startPos + offset);
                    break;
            }

            offset += width;
        }

        return new ReadOperandsResult(operands, offset);
    }

    /**
     * 讀取操作數的結果
     */
    public static class ReadOperandsResult {
        public final int[] operands;
        public final int bytesRead;

        public ReadOperandsResult(int[] operands, int bytesRead) {
            this.operands = operands;
            this.bytesRead = bytesRead;
        }
    }
}
