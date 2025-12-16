package com.monkey.vm;

import com.monkey.ast.Program;
import com.monkey.compiler.Bytecode;
import com.monkey.compiler.Compiler;
import com.monkey.lexer.Lexer;
import com.monkey.object.BooleanObject;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 虛擬機測試
 * Chapter 3: Compiling Expressions (擴展)
 */
public class VMTest {

    /**
     * 測試整數算術運算
     */
    @Test
    public void testIntegerArithmetic() {
        VMTestCase[] tests = new VMTestCase[]{
                // 基本算術
                new VMTestCase("1", 1),
                new VMTestCase("2", 2),
                new VMTestCase("1 + 2", 3),

                // 四則運算
                new VMTestCase("1 - 2", -1),
                new VMTestCase("1 * 2", 2),
                new VMTestCase("4 / 2", 2),
                new VMTestCase("50 / 2 * 2 + 10 - 5", 55),
                new VMTestCase("5 + 5 + 5 + 5 - 10", 10),
                new VMTestCase("2 * 2 * 2 * 2 * 2", 32),
                new VMTestCase("5 * 2 + 10", 20),
                new VMTestCase("5 + 2 * 10", 25),
                new VMTestCase("5 * (2 + 10)", 60),

                // 前綴運算
                new VMTestCase("-5", -5),
                new VMTestCase("-10", -10),
                new VMTestCase("-50 + 100 + -50", 0),
                new VMTestCase("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50)
        };

        runVMTests(tests);
    }

    /**
     * 測試布爾表達式
     */
    @Test
    public void testBooleanExpressions() {
        VMTestCase[] tests = new VMTestCase[]{
                // 布爾字面量
                new VMTestCase("true", true),
                new VMTestCase("false", false),

                // 整數比較
                new VMTestCase("1 < 2", true),
                new VMTestCase("1 > 2", false),
                new VMTestCase("1 < 1", false),
                new VMTestCase("1 > 1", false),
                new VMTestCase("1 == 1", true),
                new VMTestCase("1 != 1", false),
                new VMTestCase("1 == 2", false),
                new VMTestCase("1 != 2", true),

                // 布爾值比較
                new VMTestCase("true == true", true),
                new VMTestCase("false == false", true),
                new VMTestCase("true == false", false),
                new VMTestCase("true != false", true),
                new VMTestCase("false != true", true),
                new VMTestCase("(1 < 2) == true", true),
                new VMTestCase("(1 < 2) == false", false),
                new VMTestCase("(1 > 2) == true", false),
                new VMTestCase("(1 > 2) == false", true),

                // 邏輯非
                new VMTestCase("!true", false),
                new VMTestCase("!false", true),
                new VMTestCase("!5", false),
                new VMTestCase("!!true", true),
                new VMTestCase("!!false", false),
                new VMTestCase("!!5", true),

                // 複雜表達式 還未實現
//                new VMTestCase("!(if (false) { 5; })", true)
        };

        runVMTests(tests);
    }

    /**
     * 運行虛擬機測試用例
     */
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

            // Chapter 3: 使用 lastPoppedStackElem 獲取結果
            // 因為表達式語句會 OpPop
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
        }
    }

    private void testIntegerObject(long expected, MonkeyObject actual) {
        assertTrue( actual instanceof IntegerObject,
                "object is not Integer. got=" + actual.getClass());

        IntegerObject result = (IntegerObject) actual;
        assertEquals(expected, result.getValue(), String.format("object has wrong value. got=%d, want=%d",
                result.getValue(), expected));
    }

    private void testBooleanObject(boolean expected, MonkeyObject actual) {
        assertTrue(actual instanceof BooleanObject,
                "object is not Boolean. got=" + actual.getClass());

        BooleanObject result = (BooleanObject) actual;
        assertEquals(
                expected,
                result.getValue(),
                String.format(
                        "object has wrong value. got=%b, want=%b",
                        result.getValue(), expected
                )
        );
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
