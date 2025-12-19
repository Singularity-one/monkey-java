package com.monkey.vm;

import com.monkey.ast.Program;
import com.monkey.compiler.Compiler;
import com.monkey.lexer.Lexer;
import com.monkey.object.BooleanObject;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;
import com.monkey.object.NullObject;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 虛擬機測試
 * Chapter 5: Keeping Track of Names
 */
public class VMTest {

    @Test
    public void testIntegerArithmetic() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("1", 1),
                new VMTestCase("2", 2),
                new VMTestCase("1 + 2", 3)
        };
        runVMTests(tests);
    }

    @Test
    public void testBooleanExpressions() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("true", true),
                new VMTestCase("false", false),
                new VMTestCase("1 < 2", true)
        };
        runVMTests(tests);
    }

    @Test
    public void testConditionals() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("if (true) { 10 }", 10),
                new VMTestCase("if (true) { 10 } else { 20 }", 10),
                new VMTestCase("if (false) { 10 } else { 20 }", 20)
        };
        runVMTests(tests);
    }

    /**
     * Chapter 5: 測試全局變量
     */
    @Test
    public void testGlobalLetStatements() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("let one = 1; one", 1),
                new VMTestCase("let one = 1; let two = 2; one + two", 3),
                new VMTestCase("let one = 1; let two = one + one; one + two", 3)
        };
        runVMTests(tests);
    }

    private void runVMTests(VMTestCase[] tests) {
        for (VMTestCase tt : tests) {
            Program program = parse(tt.input);

            Compiler comp = new Compiler();
            try {
                comp.compile(program);
            } catch (Compiler.CompilerException e) {
                fail("compiler error: " + e.getMessage());
            }

            VM vm = new VM(comp.bytecode());
            try {
                vm.run();
            } catch (VM.VMException e) {
                fail("vm error: " + e.getMessage());
            }

            MonkeyObject stackElem = vm.lastPoppedStackElem();
            testExpectedObject(tt.expected, stackElem);
        }
    }

    private Program parse(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        return p.parseProgram();
    }

    private void testExpectedObject(Object expected, MonkeyObject actual) {
        if (expected instanceof Integer) {
            testIntegerObject((long) (int) expected, actual);
        } else if (expected instanceof Boolean) {
            testBooleanObject((boolean) expected, actual);
        } else if (expected instanceof NullObject) {
            testNullObject(actual);
        }
    }

    private void testIntegerObject(long expected, MonkeyObject actual) {
        assertTrue(actual instanceof IntegerObject,
                "object is not Integer. got=" + actual.getClass());

        IntegerObject result = (IntegerObject) actual;
        assertEquals(expected, result.getValue(),
                String.format("object has wrong value. got=%d, want=%d",
                        result.getValue(), expected));
    }

    private void testBooleanObject(boolean expected, MonkeyObject actual) {
        assertTrue(actual instanceof BooleanObject,
                "object is not Boolean. got=" + actual.getClass());

        BooleanObject result = (BooleanObject) actual;
        assertEquals(expected, result.getValue(),
                String.format("object has wrong value. got=%b, want=%b",
                        result.getValue(), expected));
    }

    private void testNullObject(MonkeyObject actual) {
        assertTrue(actual instanceof NullObject,
                "object is not Null. got=" + actual.getClass());
    }

    private static class VMTestCase {
        String input;
        Object expected;

        VMTestCase(String input, Object expected) {
            this.input = input;
            this.expected = expected;
        }
    }
}