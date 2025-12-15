package com.monkey.code;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 字節碼指令測試
 * Chapter 2: Hello Bytecode!
 */
public class InstructionsTest {

    /**
     * 測試 make 函數 - 創建字節碼指令
     *
     * 這個測試驗證我們能正確地將操作碼和操作數編碼為字節序列
     */
    @Test
    public void testMake() {
        TestCase[] tests = new TestCase[]{
                // 測試 OpConstant:
                // 輸入: 操作碼 OP_CONSTANT, 操作數 65534
                // 輸出: [0x00, 0xFF, 0xFE]
                //       ^^^^  ^^^^^^^^^^^
                //       操作碼  操作數(大端序)
                new TestCase(
                        Opcode.OP_CONSTANT,
                        new int[]{65534},
                        new byte[]{
                                Opcode.OP_CONSTANT.getValue(),  // 0x00
                                (byte) 255,                      // 0xFF (高字節)
                                (byte) 254                       // 0xFE (低字節)
                        }
                ),

                // 測試 OpAdd: 只有操作碼,無操作數
                new TestCase(
                        Opcode.OP_ADD,
                        new int[]{},
                        new byte[]{Opcode.OP_ADD.getValue()}
                ),

                // 測試 OpPop: 只有操作碼,無操作數
                new TestCase(
                        Opcode.OP_POP,
                        new int[]{},
                        new byte[]{Opcode.OP_POP.getValue()}
                )
        };

        for (TestCase tt : tests) {
            byte[] instruction = Instructions.make(tt.op, tt.operands);

            assertEquals(tt.expected.length, instruction.length, "instruction has wrong length");

            for (int i = 0; i < tt.expected.length; i++) {
                assertEquals(tt.expected[i], instruction[i], String.format("wrong byte at pos %d", i));
            }
        }
    }

    /**
     * 測試指令的字符串表示(反彙編)
     *
     * 這是調試的關鍵工具!
     * 我們希望看到可讀的輸出,而不是原始字節
     */
    @Test
    public void testInstructionsString() {
        Instructions[] instructions = new Instructions[]{
                new Instructions(Instructions.make(Opcode.OP_ADD)),
                new Instructions(Instructions.make(Opcode.OP_CONSTANT, 2)),
                new Instructions(Instructions.make(Opcode.OP_CONSTANT, 65535)),
                new Instructions(Instructions.make(Opcode.OP_POP))
        };

        // 期望的輸出格式:
        // 0000 OpAdd
        // 0001 OpConstant 2
        // 0004 OpConstant 65535
        // 0007 OpPop
        String expected =
                "0000 OpAdd\n" +
                        "0001 OpConstant 2\n" +
                        "0004 OpConstant 65535\n" +
                        "0007 OpPop\n";

        // 連接所有指令
        Instructions concatenated = new Instructions();
        for (Instructions ins : instructions) {
            concatenated.append(ins.toByteArray());
        }
        assertEquals(expected, concatenated.toString(), "instructions wrongly formatted");
    }

    /**
     * 測試讀取操作數
     *
     * 這是 make() 的反向操作
     * VM 需要用這個方法來解碼指令
     */
    @Test
    public void testReadOperands() {
        TestCase2[] tests = new TestCase2[]{
                // 測試讀取 2 字節操作數
                new TestCase2(Opcode.OP_CONSTANT, new int[]{65535}, 2)
        };

        for (TestCase2 tt : tests) {
            // 1. 創建完整指令
            byte[] instruction = Instructions.make(tt.op, tt.operands);

            // 2. 獲取操作碼定義
            Instructions.Definition def = Instructions.lookup(tt.op.getValue());

            // 3. 提取操作數部分(跳過第一個字節的操作碼)
            byte[] operandBytes = new byte[instruction.length - 1];
            System.arraycopy(instruction, 1, operandBytes, 0, operandBytes.length);

            // 4. 讀取操作數
            Instructions.ReadOperandsResult result =
                    Instructions.readOperands(def, operandBytes);

            // 5. 驗證讀取的字節數
            assertEquals(tt.bytesRead, result.bytesRead, "bytesRead wrong");

            // 6. 驗證解碼的操作數值
            for (int i = 0; i < tt.operands.length; i++) {
                assertEquals(tt.operands[i], result.operands[i], "operand wrong");
            }
        }
    }

    /**
     * 測試用例結構
     */
    private static class TestCase {
        Opcode op;
        int[] operands;
        byte[] expected;

        TestCase(Opcode op, int[] operands, byte[] expected) {
            this.op = op;
            this.operands = operands;
            this.expected = expected;
        }
    }

    private static class TestCase2 {
        Opcode op;
        int[] operands;
        int bytesRead;

        TestCase2(Opcode op, int[] operands, int bytesRead) {
            this.op = op;
            this.operands = operands;
            this.bytesRead = bytesRead;
        }
    }
}