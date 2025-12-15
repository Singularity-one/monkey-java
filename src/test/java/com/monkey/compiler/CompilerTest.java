package com.monkey.compiler;

import com.monkey.ast.Program;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.lexer.Lexer;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;
import com.monkey.parser.Parser;
import java.util.List;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 編譯器測試
 * Chapter 2: Hello Bytecode!
 */
public class CompilerTest {

    /**
     * 測試整數算術運算編譯
     *
     * 這是第二章的核心測試!
     * 我們要確保 "1 + 2" 能正確編譯為字節碼
     */
    @Test
    public void testIntegerArithmetic() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                // 測試 "1 + 2"
                //
                // AST:
                //   InfixExpression(+)
                //     Left: IntegerLiteral(1)
                //     Right: IntegerLiteral(2)
                //
                // 字節碼:
                //   OpConstant 0  ; 載入常量 1
                //   OpConstant 1  ; 載入常量 2
                //   OpAdd         ; 執行加法
                new CompilerTestCase(
                        "1 + 2",
                        new Object[]{1, 2},          // 常量池: [1, 2]
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_ADD)
                        }
                ),

                // 測試 "1; 2"
                // 兩個表達式語句
                new CompilerTestCase(
                        "1; 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1)
                        }
                ),

                // 測試 "1 - 2"
                new CompilerTestCase(
                        "1 - 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_SUB)
                        }
                ),

                // 測試 "1 * 2"
                new CompilerTestCase(
                        "1 * 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_MUL)
                        }
                ),

                // 測試 "2 / 1"
                new CompilerTestCase(
                        "2 / 1",
                        new Object[]{2, 1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_DIV)
                        }
                )
        };

        runCompilerTests(tests);
    }

    /**
     * 運行編譯器測試用例
     */
    private void runCompilerTests(CompilerTestCase[] tests) {
        for (CompilerTestCase tt : tests) {
            // 1. 解析源代碼
            Program program = parse(tt.input);

            // 2. 編譯
            Compiler compiler = new Compiler();
            try {
                compiler.compile(program);
            } catch (Compiler.CompilerException e) {
                fail("compiler error: " + e.getMessage());
            }

            // 3. 獲取編譯結果
            Bytecode bytecode = compiler.bytecode();

            // 4. 驗證指令
            testInstructions(tt.expectedInstructions, bytecode.getInstructions());

            // 5. 驗證常量池
            testConstants(tt.expectedConstants, bytecode.getConstants());
        }
    }

    /**
     * 解析 Monkey 代碼
     */
    private Program parse(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        return p.parseProgram();
    }

    /**
     * 驗證生成的指令是否正確
     */
    private void testInstructions(byte[][] expected, Instructions actual) {
        Instructions concatenated = concatInstructions(expected);

        assertEquals(
                concatenated.size(),
                actual.size(),
                String.format("wrong instructions length.\nwant=%s\ngot=%s",
                        concatenated, actual)
        );

        for (int i = 0; i < concatenated.size(); i++) {
            assertEquals(
                    concatenated.get(i),
                    actual.get(i),
                    String.format("wrong instruction at %d.\nwant=%s\ngot=%s",
                            i, concatenated, actual)
            );
        }
    }

    /**
     * 連接多條指令為單個指令序列
     */
    private Instructions concatInstructions(byte[][] s) {
        Instructions out = new Instructions();
        for (byte[] ins : s) {
            out.append(ins);
        }
        return out;
    }

    /**
     * 驗證常量池是否正確
     */
    private void testConstants(Object[] expected, List<MonkeyObject> actual) {
        assertEquals(
                expected.length,
                actual.size(),
                "wrong number of constants"
        );

        for (int i = 0; i < expected.length; i++) {
            Object constant = expected[i];
            if (constant instanceof Integer) {
                testIntegerObject((long) (int) constant, actual.get(i));
            }
        }
    }

    /**
     * 驗證整數對象
     */
    private void testIntegerObject(long expected, MonkeyObject actual) {
        assertTrue(actual instanceof IntegerObject, "object is not Integer. got=" + actual.getClass());

        IntegerObject result = (IntegerObject) actual;

        assertTrue(actual instanceof IntegerObject, "object is not Integer. got=" + actual.getClass());
        assertEquals(expected, result.getValue(), "object has wrong value");
    }

    /**
     * 測試用例結構
     */
    private static class CompilerTestCase {
        String input;                  // 源代碼
        Object[] expectedConstants;    // 期望的常量池
        byte[][] expectedInstructions; // 期望的指令

        CompilerTestCase(String input, Object[] expectedConstants, byte[][] expectedInstructions) {
            this.input = input;
            this.expectedConstants = expectedConstants;
            this.expectedInstructions = expectedInstructions;
        }
    }
}