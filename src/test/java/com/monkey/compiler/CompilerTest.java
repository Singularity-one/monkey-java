package com.monkey.compiler;

import com.monkey.ast.Program;
import com.monkey.code.Instructions;
import com.monkey.code.Opcode;
import com.monkey.lexer.Lexer;
import com.monkey.object.*;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 編譯器測試
 * Chapter 6: String, Array and Hash (擴展)
 */
public class CompilerTest {

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
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testBooleanExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "true",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_TRUE),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testConditionals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "if (true) { 10 }; 3333;",
                        new Object[]{10, 3333},
                        new byte[][]{
                                Instructions.make(Opcode.OP_TRUE),
                                Instructions.make(Opcode.OP_JUMP_NOT_TRUTHY, 10),
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_JUMP, 11),
                                Instructions.make(Opcode.OP_NULL),
                                Instructions.make(Opcode.OP_POP),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    @Test
    public void testGlobalLetStatements() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "let one = 1; let two = 2;",
                        new Object[]{1, 2},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 1)
                        }
                ),
                new CompilerTestCase(
                        "let one = 1; one;",
                        new Object[]{1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_GET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "let one = 1; let two = one; two;",
                        new Object[]{1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_GET_GLOBAL, 0),
                                Instructions.make(Opcode.OP_SET_GLOBAL, 1),
                                Instructions.make(Opcode.OP_GET_GLOBAL, 1),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };

        runCompilerTests(tests);
    }

    /**
     * Chapter 6: 測試字串表達式編譯
     */
    @Test
    public void testStringExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "\"monkey\"",
                        new Object[]{"monkey"},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "\"mon\" + \"key\"",
                        new Object[]{"mon", "key"},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_ADD),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };

        runCompilerTests(tests);
    }

    /**
     * Chapter 6: 測試陣列字面量編譯
     */
    @Test
    public void testArrayLiterals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "[]",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_ARRAY, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "[1, 2, 3]",
                        new Object[]{1, 2, 3},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_ARRAY, 3),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "[1 + 2, 3 - 4, 5 * 6]",
                        new Object[]{1, 2, 3, 4, 5, 6},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_ADD),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_CONSTANT, 3),
                                Instructions.make(Opcode.OP_SUB),
                                Instructions.make(Opcode.OP_CONSTANT, 4),
                                Instructions.make(Opcode.OP_CONSTANT, 5),
                                Instructions.make(Opcode.OP_MUL),
                                Instructions.make(Opcode.OP_ARRAY, 3),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };

        runCompilerTests(tests);
    }

    /**
     * Chapter 6: 測試雜湊表字面量編譯
     */
    @Test
    public void testHashLiterals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "{}",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_HASH, 0),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "{1: 2, 3: 4, 5: 6}",
                        new Object[]{1, 2, 3, 4, 5, 6},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_CONSTANT, 3),
                                Instructions.make(Opcode.OP_CONSTANT, 4),
                                Instructions.make(Opcode.OP_CONSTANT, 5),
                                Instructions.make(Opcode.OP_HASH, 6),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "{1: 2 + 3, 4: 5 * 6}",
                        new Object[]{1, 2, 3, 4, 5, 6},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_ADD),
                                Instructions.make(Opcode.OP_CONSTANT, 3),
                                Instructions.make(Opcode.OP_CONSTANT, 4),
                                Instructions.make(Opcode.OP_CONSTANT, 5),
                                Instructions.make(Opcode.OP_MUL),
                                Instructions.make(Opcode.OP_HASH, 4),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };

        runCompilerTests(tests);
    }

    /**
     * Chapter 6: 測試索引表達式編譯
     */
    @Test
    public void testIndexExpressions() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                new CompilerTestCase(
                        "[1, 2, 3][1 + 1]",
                        new Object[]{1, 2, 3, 1, 1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_ARRAY, 3),
                                Instructions.make(Opcode.OP_CONSTANT, 3),
                                Instructions.make(Opcode.OP_CONSTANT, 4),
                                Instructions.make(Opcode.OP_ADD),
                                Instructions.make(Opcode.OP_INDEX),
                                Instructions.make(Opcode.OP_POP)
                        }
                ),
                new CompilerTestCase(
                        "{1: 2}[2 - 1]",
                        new Object[]{1, 2, 2, 1},
                        new byte[][]{
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                Instructions.make(Opcode.OP_HASH, 2),
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                Instructions.make(Opcode.OP_CONSTANT, 3),
                                Instructions.make(Opcode.OP_SUB),
                                Instructions.make(Opcode.OP_INDEX),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };

        runCompilerTests(tests);
    }

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

    private Program parse(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        return p.parseProgram();
    }

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

    private Instructions concatInstructions(byte[][] s) {
        Instructions out = new Instructions();
        for (byte[] ins : s) {
            out.append(ins);
        }
        return out;
    }

    private void testConstants(Object[] expected, List<MonkeyObject> actual) {
        assertEquals(expected.length, actual.size(),
                "wrong number of constants");

        for (int i = 0; i < expected.length; i++) {
            Object constant = expected[i];
            if (constant instanceof Integer) {
                testIntegerObject((long) (int) constant, actual.get(i));
            } else if (constant instanceof Boolean) {
                testBooleanObject((boolean) constant, actual.get(i));
            } else if (constant instanceof String) {
                testStringObject((String) constant, actual.get(i));
            }
        }
    }

    private void testIntegerObject(long expected, MonkeyObject actual) {
        assertTrue(actual instanceof IntegerObject,
                "object is not Integer. got=" + actual.getClass());

        IntegerObject result = (IntegerObject) actual;
        assertEquals(expected, result.getValue(),
                "object has wrong value");
    }

    private void testBooleanObject(boolean expected, MonkeyObject actual) {
        assertTrue(actual instanceof BooleanObject,
                "object is not Boolean. got=" + actual.getClass());

        BooleanObject result = (BooleanObject) actual;
        assertEquals(expected, result.getValue(),
                "object has wrong value");
    }

    /**
     * Chapter 6: 測試字串常量
     */
    private void testStringObject(String expected, MonkeyObject actual) {
        assertTrue(actual instanceof StringObject,
                "object is not String. got=" + actual.getClass());

        StringObject result = (StringObject) actual;
        assertEquals(expected, result.getValue(),
                "object has wrong value");
    }

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