package com.monkey.compiler;

import com.monkey.ast.Program;
import com.monkey.code.*;
import com.monkey.lexer.Lexer;
import com.monkey.object.*;
import com.monkey.parser.Parser;
import com.monkey.vm.VM;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 編譯器和 VM 測試
 */
public class CompilerTest {

    @Test
    public void testIntegerArithmetic() throws Exception {
        Object[][] tests = {
                {"1", 1L},
                {"2", 2L},
                {"1 + 2", 3L},
                {"1 - 2", -1L},
                {"1 * 2", 2L},
                {"4 / 2", 2L},
                {"50 / 2 * 2 + 10 - 5", 55L},
                {"5 + 5 + 5 + 5 - 10", 10L},
                {"2 * 2 * 2 * 2 * 2", 32L},
                {"5 * 2 + 10", 20L},
                {"5 + 2 * 10", 25L},
                {"5 * (2 + 10)", 60L},
                {"-5", -5L},
                {"-10", -10L},
                {"-50 + 100 + -50", 0L}
        };

        runVmTests(tests);
    }

    @Test
    public void testBooleanExpressions() throws Exception {
        Object[][] tests = {
                {"true", true},
                {"false", false},
                {"1 < 2", true},
                {"1 > 2", false},
                {"1 < 1", false},
                {"1 > 1", false},
                {"1 == 1", true},
                {"1 != 1", false},
                {"1 == 2", false},
                {"1 != 2", true},
                {"true == true", true},
                {"false == false", true},
                {"true == false", false},
                {"true != false", true},
                {"false != true", true},
                {"(1 < 2) == true", true},
                {"(1 < 2) == false", false},
                {"(1 > 2) == true", false},
                {"(1 > 2) == false", true},
                {"!true", false},
                {"!false", true},
                {"!5", false},
                {"!!true", true},
                {"!!false", false},
                {"!!5", true}
        };

        runVmTests(tests);
    }

    @Test
    public void testConditionals() throws Exception {
        Object[][] tests = {
                {"if (true) { 10 }", 10L},
                {"if (true) { 10 } else { 20 }", 10L},
                {"if (false) { 10 } else { 20 }", 20L},
                {"if (1) { 10 }", 10L},
                {"if (1 < 2) { 10 }", 10L},
                {"if (1 < 2) { 10 } else { 20 }", 10L},
                {"if (1 > 2) { 10 } else { 20 }", 20L},
                {"if (1 > 2) { 10 }", null},
                {"if (false) { 10 }", null},
                {"if ((if (false) { 10 })) { 10 } else { 20 }", 20L}
        };

        runVmTests(tests);
    }

    @Test
    public void testMakeInstruction() {
        Object[][] tests = {
                {
                        Opcode.OpConstant,
                        new int[]{65534},
                        new byte[]{(byte) Opcode.OpConstant.getCode(), (byte) 0xFF, (byte) 0xFE}
                },
                {
                        Opcode.OpAdd,
                        new int[]{},
                        new byte[]{(byte) Opcode.OpAdd.getCode()}
                }
        };

        for (Object[] tt : tests) {
            Opcode op = (Opcode) tt[0];
            int[] operands = (int[]) tt[1];
            byte[] expected = (byte[]) tt[2];

            byte[] instruction = Code.make(op, operands);

            assertEquals(expected.length, instruction.length,
                    "instruction has wrong length");

            for (int i = 0; i < expected.length; i++) {
                assertEquals(expected[i], instruction[i],
                        String.format("wrong byte at pos %d", i));
            }
        }
    }

    @Test
    public void testInstructionsString() {
        Instructions instructions = new Instructions();
        instructions.addAll(Code.make(Opcode.OpAdd));
        instructions.addAll(Code.make(Opcode.OpConstant, 2));
        instructions.addAll(Code.make(Opcode.OpConstant, 65535));

        String expected = """
            0000 OpAdd
            0001 OpConstant 2
            0004 OpConstant 65535
            """;

        assertEquals(expected, instructions.toString());
    }

    // 輔助方法

    private void runVmTests(Object[][] tests) throws Exception {
        for (Object[] tt : tests) {
            String input = (String) tt[0];
            Object expected = tt[1];

            Program program = parse(input);
            Compiler compiler = new Compiler();
            compiler.compile(program);

            VM vm = new VM(compiler.bytecode());
            vm.run();

            MonkeyObject stackElem = vm.lastPoppedStackElem();
            testExpectedObject(expected, stackElem);
        }
    }

    private Program parse(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        return p.parseProgram();
    }

    private void testExpectedObject(Object expected, MonkeyObject actual) {
        if (expected instanceof Long) {
            testIntegerObject((Long) expected, actual);
        } else if (expected instanceof Boolean) {
            testBooleanObject((Boolean) expected, actual);
        } else if (expected == null) {
            assertSame(NullObject.NULL, actual, "object is not Null");
        }
    }

    private void testIntegerObject(long expected, MonkeyObject actual) {
        assertTrue(actual instanceof IntegerObject,
                "object is not Integer. got=" + actual.getClass().getName());

        IntegerObject result = (IntegerObject) actual;
        assertEquals(expected, result.getValue(),
                "object has wrong value. got=" + result.getValue() + ", want=" + expected);
    }

    private void testBooleanObject(boolean expected, MonkeyObject actual) {
        assertTrue(actual instanceof BooleanObject,
                "object is not Boolean. got=" + actual.getClass().getName());

        BooleanObject result = (BooleanObject) actual;
        assertEquals(expected, result.getValue(),
                "object has wrong value. got=" + result.getValue() + ", want=" + expected);
    }
}