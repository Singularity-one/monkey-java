package com.monkey.code;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Instructions 表示字節碼指令序列
 * Chapter 2: Hello Bytecode!
 *
 * 字節碼是一系列指令的集合,每條指令由操作碼和可選的操作數組成
 */
public class Instructions {
    private final List<Byte> bytes;

    // 操作碼定義映射 - 定義每個操作碼的名稱和操作數寬度
    private static final Map<Opcode, Definition> DEFINITIONS = new HashMap<>();

    static {
        // OpConstant: 載入常量到堆疊
        // 操作數: 2 字節的常量池索引 (支持 0-65535 個常量)
        DEFINITIONS.put(Opcode.OP_CONSTANT, new Definition("OpConstant", new int[]{2}));

        // OpAdd: 彈出兩個值,相加後推入結果
        // 操作數: 無
        DEFINITIONS.put(Opcode.OP_ADD, new Definition("OpAdd", new int[]{}));

        // OpPop: 彈出堆疊頂部元素
        // 操作數: 無
        DEFINITIONS.put(Opcode.OP_POP, new Definition("OpPop", new int[]{}));

        // Chapter 3 預留
        DEFINITIONS.put(Opcode.OP_SUB, new Definition("OpSub", new int[]{}));
        DEFINITIONS.put(Opcode.OP_MUL, new Definition("OpMul", new int[]{}));
        DEFINITIONS.put(Opcode.OP_DIV, new Definition("OpDiv", new int[]{}));
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

    /**
     * 查找操作碼的定義
     *
     * @param op 操作碼字節
     * @return 操作碼定義
     * @throws IllegalArgumentException 如果操作碼未定義
     */
    public static Definition lookup(byte op) {
        Opcode opcode = Opcode.fromByte(op);
        Definition def = DEFINITIONS.get(opcode);
        if (def == null) {
            throw new IllegalArgumentException("Opcode " + op + " undefined");
        }
        return def;
    }

    /**
     * 創建一條字節碼指令
     *
     * 這是編譯器生成字節碼的核心方法
     *
     * @param op 操作碼
     * @param operands 操作數數組
     * @return 編碼後的指令字節數組
     */
    public static byte[] make(Opcode op, int... operands) {
        Definition def = DEFINITIONS.get(op);
        if (def == null) {
            return new byte[]{};
        }

        // 計算指令總長度: 1 字節操作碼 + 所有操作數的字節數
        int instructionLen = 1;
        for (int w : def.getOperandWidths()) {
            instructionLen += w;
        }

        // 分配指令字節數組
        byte[] instruction = new byte[instructionLen];
        instruction[0] = op.getValue();

        // 編碼每個操作數
        int offset = 1;
        for (int i = 0; i < operands.length; i++) {
            int width = def.getOperandWidths()[i];
            switch (width) {
                case 2:
                    // 2 字節操作數 - 使用大端序編碼
                    putUint16(instruction, offset, operands[i]);
                    break;
                case 1:
                    // 1 字節操作數
                    instruction[offset] = (byte) (operands[i] & 0xFF);
                    break;
            }
            offset += width;
        }

        return instruction;
    }

    /**
     * 從指令中讀取 16 位無符號整數(大端序)
     *
     * 用於 VM 解碼指令操作數
     *
     * @param ins 指令字節數組
     * @return 解碼後的整數值
     */
    public static int readUint16(byte[] ins) {
        return ((ins[0] & 0xFF) << 8) | (ins[1] & 0xFF);
    }

    /**
     * 從指令中讀取所有操作數
     *
     * 這是 make() 的反向操作
     *
     * @param def 操作碼定義
     * @param ins 指令字節數組(不包含操作碼)
     * @return 解碼結果,包含操作數數組和讀取的字節數
     */
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

    /**
     * 寫入 16 位無符號整數(大端序)
     *
     * 大端序: 高位字節在前,低位字節在後
     * 例如: 65534 = 0xFFFE -> [0xFF, 0xFE]
     */
    private static void putUint16(byte[] arr, int offset, int value) {
        arr[offset] = (byte) ((value >> 8) & 0xFF);      // 高字節
        arr[offset + 1] = (byte) (value & 0xFF);         // 低字節
    }

    /**
     * 追加指令到指令序列
     */
    public void append(byte[] ins) {
        for (byte b : ins) {
            bytes.add(b);
        }
    }

    /**
     * 獲取指令序列長度
     */
    public int size() {
        return bytes.size();
    }

    /**
     * 獲取指定位置的字節
     */
    public byte get(int index) {
        return bytes.get(index);
    }

    /**
     * 轉換為字節數組
     */
    public byte[] toByteArray() {
        byte[] result = new byte[bytes.size()];
        for (int i = 0; i < bytes.size(); i++) {
            result[i] = bytes.get(i);
        }
        return result;
    }

    /**
     * 反彙編 - 將字節碼轉換為可讀的文本格式
     *
     * 這是調試的關鍵工具!
     *
     * 輸出格式:
     * 0000 OpConstant 1
     * 0003 OpConstant 2
     * 0006 OpAdd
     * 0007 OpPop
     */
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

            // 讀取操作數部分
            byte[] operandBytes = new byte[bytes.size() - i - 1];
            for (int j = 0; j < operandBytes.length; j++) {
                operandBytes[j] = bytes.get(i + 1 + j);
            }

            ReadOperandsResult result = readOperands(def, operandBytes);

            // 格式化輸出: 位置 + 指令
            out.append(String.format("%04d %s\n", i, fmtInstruction(def, result.operands)));

            // 移動到下一條指令
            i += 1 + result.bytesRead;
        }

        return out.toString();
    }

    /**
     * 格式化單條指令
     */
    private String fmtInstruction(Definition def, int[] operands) {
        int operandCount = def.getOperandWidths().length;

        if (operands.length != operandCount) {
            return String.format("ERROR: operand len %d does not match defined %d\n",
                    operands.length, operandCount);
        }

        switch (operandCount) {
            case 0:
                // 無操作數指令: OpAdd, OpPop 等
                return def.getName();
            case 1:
                // 單操作數指令: OpConstant 等
                return String.format("%s %d", def.getName(), operands[0]);
            case 2:
                // 雙操作數指令 (未來使用)
                return String.format("%s %d %d", def.getName(), operands[0], operands[1]);
        }

        return String.format("ERROR: unhandled operandCount for %s\n", def.getName());
    }

    /**
     * 操作數讀取結果
     */
    public static class ReadOperandsResult {
        public final int[] operands;    // 解碼後的操作數
        public final int bytesRead;     // 讀取的字節數

        public ReadOperandsResult(int[] operands, int bytesRead) {
            this.operands = operands;
            this.bytesRead = bytesRead;
        }
    }

    /**
     * 操作碼定義
     *
     * 定義操作碼的名稱和操作數結構
     */
    public static class Definition {
        private final String name;              // 操作碼名稱(用於調試)
        private final int[] operandWidths;      // 每個操作數的字節寬度

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
