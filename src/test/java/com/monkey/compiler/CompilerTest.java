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
 * Chapter 4: Conditionals
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
                ),
                new CompilerTestCase(
                        "false",
                        new Object[]{},
                        new byte[][]{
                                Instructions.make(Opcode.OP_FALSE),
                                Instructions.make(Opcode.OP_POP)
                        }
                )
        };
        runCompilerTests(tests);
    }

    /**
     * Chapter 4: 測試條件語句編譯
     */
    @Test
    public void testConditionals() {
        CompilerTestCase[] tests = new CompilerTestCase[]{
                // if (true) { 10 }; 3333;
                new CompilerTestCase(
                        "if (true) { 10 }; 3333;",
                        new Object[]{10, 3333},
                        new byte[][]{
                                // 0000
                                Instructions.make(Opcode.OP_TRUE),
                                // 0001
                                Instructions.make(Opcode.OP_JUMP_NOT_TRUTHY, 10),
                                // 0004
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                // 0007
                                Instructions.make(Opcode.OP_JUMP, 11),
                                // 0010
                                Instructions.make(Opcode.OP_NULL),
                                // 0011
                                Instructions.make(Opcode.OP_POP),
                                // 0012
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                // 0015
                                Instructions.make(Opcode.OP_POP)
                        }
                ),

                // if (true) { 10 } else { 20 }; 3333;
                new CompilerTestCase(
                        "if (true) { 10 } else { 20 }; 3333;",
                        new Object[]{10, 20, 3333},
                        new byte[][]{
                                // 0000
                                Instructions.make(Opcode.OP_TRUE),
                                // 0001
                                Instructions.make(Opcode.OP_JUMP_NOT_TRUTHY, 10),
                                // 0004
                                Instructions.make(Opcode.OP_CONSTANT, 0),
                                // 0007
                                Instructions.make(Opcode.OP_JUMP, 13),
                                // 0010
                                Instructions.make(Opcode.OP_CONSTANT, 1),
                                // 0013
                                Instructions.make(Opcode.OP_POP),
                                // 0014
                                Instructions.make(Opcode.OP_CONSTANT, 2),
                                // 0017
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