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

    /**
     * Chapter 9: 測試 OpClosure 指令的格式化
     */
    @Test
    public void testInstructionsString() {
        byte[][] instructions = {
                Instructions.make(Opcode.OP_ADD),
                Instructions.make(Opcode.OP_GET_LOCAL, 1),
                Instructions.make(Opcode.OP_CONSTANT, 2),
                Instructions.make(Opcode.OP_CONSTANT, 65535),
                Instructions.make(Opcode.OP_CLOSURE, 65535, 255)  // 測試兩個操作數
        };

        String expected = """
            0000 OpAdd
            0001 OpGetLocal 1
            0003 OpConstant 2
            0006 OpConstant 65535
            0009 OpClosure 65535 255
            """;

        Instructions concatted = new Instructions();
        for (byte[] ins : instructions) {
            concatted.append(ins);
        }

        assertEquals(expected, concatted.string(),
                "instructions wrongly formatted");
    }
}