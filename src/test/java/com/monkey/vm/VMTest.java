package com.monkey.vm;

import com.monkey.ast.Program;
import com.monkey.compiler.Bytecode;
import com.monkey.compiler.Compiler;
import com.monkey.lexer.Lexer;
import com.monkey.object.IntegerObject;
import com.monkey.object.MonkeyObject;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 虛擬機測試
 * Chapter 2: Hello Bytecode!
 */
@Disabled
public class VMTest {

    /**
     * 測試整數算術運算
     *
     * 這是端到端測試:
     * 源代碼 -> 詞法分析 -> 語法分析 -> 編譯 -> 執行 -> 驗證結果
     */
    @Test
    public void testIntegerArithmetic() {
        VMTestCase[] tests = new VMTestCase[]{
                // 基本測試
                new VMTestCase("1", 1),
                new VMTestCase("2", 2),

                // 加法測試 - 第二章的目標!
                new VMTestCase("1 + 2", 3),

                // 更多算術運算
                new VMTestCase("1 - 2", -1),
                new VMTestCase("1 * 2", 2),
                new VMTestCase("4 / 2", 2),
                new VMTestCase("50 / 2 * 2 + 10 - 5", 55),
                new VMTestCase("5 + 5 + 5 + 5 - 10", 10),
                new VMTestCase("2 * 2 * 2 * 2 * 2", 32),
                new VMTestCase("5 * 2 + 10", 20),
                new VMTestCase("5 + 2 * 10", 25),
                new VMTestCase("5 * (2 + 10)", 60),

                // 負數測試
                new VMTestCase("-5", -5),
                new VMTestCase("-10", -10),
                new VMTestCase("-50 + 100 + -50", 0),
                new VMTestCase("(5 + 10 * 2 + 15 / 3) * 2 + -10", 50)
        };

        runVMTests(tests);
    }

    /**
     * 運行虛擬機測試用例
     */
    private void runVMTests(VMTestCase[] tests) {
        for (VMTestCase tt : tests) {
            // 1. 解析源代碼
            Program program = parse(tt.input);

            // 2. 編譯
            Compiler comp = new Compiler();
            try {
                comp.compile(program);
            } catch (Compiler.CompilerException e) {
                fail("compiler error: " + e.getMessage());
            }

            // 3. 執行
            VM vm = new VM(comp.bytecode());
            try {
                vm.run();
            } catch (VM.VMException e) {
                fail("vm error: " + e.getMessage());
            }

            // 4. 驗證結果
            MonkeyObject stackElem = vm.stackTop();
            testExpectedObject(tt.expected, stackElem);
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
     * 驗證期望的對象
     */
    private void testExpectedObject(Object expected, MonkeyObject actual) {
        if (expected instanceof Integer) {
            testIntegerObject((long) (int) expected, actual);
        }
    }

    /**
     * 驗證整數對象
     */
    private void testIntegerObject(long expected, MonkeyObject actual) {
        assertTrue(actual instanceof IntegerObject, "object is not Integer. got=" + actual.getClass());

        IntegerObject result = (IntegerObject) actual;
        assertEquals(expected, result.getValue(),  String.format("object has wrong value. got=%d, want=%d",
                result.getValue(), expected));
    }

    /**
     * 測試用例結構
     */
    private static class VMTestCase {
        String input;     // 源代碼
        Object expected;  // 期望的結果

        VMTestCase(String input, Object expected) {
            this.input = input;
            this.expected = expected;
        }
    }
}
