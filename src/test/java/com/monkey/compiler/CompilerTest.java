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
 * Chapter 5: Keeping Track of Names
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

    /**
     * Chapter 5: 測試全局 let 語句編譯
     */
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