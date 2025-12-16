package com.monkey.compiler;

import com.monkey.ast.Program;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.lexer.Lexer;
import com.monkey.object.BooleanObject;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 編譯器測試
 * Chapter 3: Compiling Expressions
 */
public class CompilerTest {

    /**
     * 測試整數算術
     */
    @Test
    public void testIntegerArithmetic() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "1 + 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_ADD),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "1; 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_POP),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "1 - 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_SUB),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "1 * 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_MUL),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "2 / 1",
                        new Object[]{2, 1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_DIV),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "-1",
                        new Object[]{1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_MINUS),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
        };

        runCompilerTests(tests);
    }

    /**
     * 測試布林表達式
     */
    @Test
    public void testBooleanExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "true",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_TRUE),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "false",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_FALSE),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "1 > 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_GREATER_THAN),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "1 < 2",
                        new Object[]{2, 1},  // 注意: 操作數順序反轉
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_GREATER_THAN),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "1 == 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_EQUAL),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "1 != 2",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_NOT_EQUAL),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "true == false",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_TRUE),
                                Instructions.make(Opcode.OP_FALSE),
                                Instructions.make(Opcode.OP_EQUAL),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "true != false",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_TRUE),
                                Instructions.make(Opcode.OP_FALSE),
                                Instructions.make(Opcode.OP_NOT_EQUAL),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
                new CompilerTestCase(
                        "!true",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_TRUE),
                                Instructions.make(Opcode.OP_BANG),
                                Instructions.make(Opcode.OP_POP),
                        }
                ),
        };

        runCompilerTests(tests);
    }

    /**
     * 運行編譯器測試
     */
    private void runCompilerTests(CompilerTestCase[] tests) {
        for (CompilerTestCase tt : tests) {
            Program program = parse(tt.input);

            Compiler compiler = new Compiler();
            try {
                compiler.compile(program);
            } catch (Compiler.CompilerException e) {
                fail("compiler error: " + e.getMessage());
            }

            Bytecode bytecode = compiler.bytecode();

            testInstructions(tt.expectedInstructions, bytecode.getInstructions());
            testConstants(tt.expectedConstants, bytecode.getConstants());
        }
    }

    /**
     * 解析源代碼
     */
    private Program parse(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        return p.parseProgram();
    }

    /**
     * 測試指令
     */
    private void testInstructions(byte[][] expected, Instructions actual) {
        Instructions concatenated = concatInstructions(expected);

        assertEquals(concatenated.size(), actual.size(),
                String.format("wrong instructions length.\nwant=%s\ngot=%s",
                        concatenated, actual));

        for (int i = 0; i < concatenated.size(); i++) {
            assertEquals(concatenated.get(i), actual.get(i),
                    String.format("wrong instruction at %d.\nwant=%s\ngot=%s",
                            i, concatenated, actual));
        }
    }

    /**
     * 連接多條指令
     */
    private Instructions concatInstructions(byte[][] s) {
        Instructions out = new Instructions();
        for (byte[] ins : s) {
            out.append(ins);
        }
        return out;
    }

    /**
     * 測試常量
     */
    private void testConstants(Object[] expected, List<MonkeyObject> actual) {
        assertEquals(expected.length, actual.size(),
                "wrong number of constants");

        for (int i = 0; i < expected.length; i++) {
            Object constant = expected[i];
            if (constant instanceof Integer) {
                testIntegerObject((long) (int) constant, actual.get(i));
            } else if (constant instanceof Boolean) {
                testBooleanObject((boolean) constant, actual.get(i));
            }
        }
    }

    /**
     * 測試整數對象
     */
    private void testIntegerObject(long expected, MonkeyObject actual) {
        assertTrue(actual instanceof IntegerObject,
                "object is not Integer. got=" + actual.getClass());

        IntegerObject result = (IntegerObject) actual;
        assertEquals(expected, result.getValue(),
                String.format("object has wrong value. got=%d, want=%d",
                        result.getValue(), expected));
    }

    /**
     * 測試布林對象
     */
    private void testBooleanObject(boolean expected, MonkeyObject actual) {
        assertTrue(actual instanceof BooleanObject,
                "object is not Boolean. got=" + actual.getClass());

        BooleanObject result = (BooleanObject) actual;
        assertEquals(expected, result.getValue(),
                String.format("object has wrong value. got=%b, want=%b",
                        result.getValue(), expected));
    }

    /**
     * 編譯器測試用例
     */
    private static class CompilerTestCase {
        String input;
        Object[] expectedConstants;
        byte[][] expectedInstructions;

        CompilerTestCase(String input, Object[] expectedConstants, byte[][] expectedInstructions) {
            this.input = input;
            this.expectedConstants = expectedConstants;
            this.expectedInstructions = expectedInstructions;
        }
    }
}