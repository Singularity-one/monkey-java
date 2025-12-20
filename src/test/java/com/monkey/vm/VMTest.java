package com.monkey.vm;

import com.monkey.ast.Program;
import com.monkey.compiler.Compiler;
import com.monkey.lexer.Lexer;
import com.monkey.object.*;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 虛擬機測試
 * Chapter 7: Functions (擴展)
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

    @Test
    public void testGlobalLetStatements() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("let one = 1; one", 1),
                new VMTestCase("let one = 1; let two = 2; one + two", 3),
                new VMTestCase("let one = 1; let two = one + one; one + two", 3)
        };
        runVMTests(tests);
    }

    @Test
    public void testStringExpressions() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("\"monkey\"", "monkey"),
                new VMTestCase("\"mon\" + \"key\"", "monkey"),
                new VMTestCase("\"mon\" + \"key\" + \"banana\"", "monkeybanana")
        };
        runVMTests(tests);
    }

    @Test
    public void testArrayLiterals() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("[]", new int[]{}),
                new VMTestCase("[1, 2, 3]", new int[]{1, 2, 3}),
                new VMTestCase("[1 + 2, 3 * 4, 5 + 6]", new int[]{3, 12, 11})
        };
        runVMTests(tests);
    }

    @Test
    public void testHashLiterals() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("{}", new HashMap<>()),
                new VMTestCase(
                        "{1: 2, 2: 3}",
                        new HashMap<HashKey, Integer>() {{
                            put(new IntegerObject(1).hashKey(), 2);
                            put(new IntegerObject(2).hashKey(), 3);
                        }}
                )
        };
        runVMTests(tests);
    }

    @Test
    public void testIndexExpressions() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase("[1, 2, 3][1]", 2),
                new VMTestCase("[1, 2, 3][0 + 2]", 3),
                new VMTestCase("[[1, 1, 1]][0][0]", 1),
                new VMTestCase("[][0]", new NullObject()),
                new VMTestCase("[1, 2, 3][99]", new NullObject()),
                new VMTestCase("{1: 1, 2: 2}[1]", 1),
                new VMTestCase("{1: 1}[0]", new NullObject())
        };
        runVMTests(tests);
    }

    /**
     * Chapter 7: 測試函數調用 (無參數)
     */
    @Test
    public void testCallingFunctionsWithoutArguments() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase(
                        "let fivePlusTen = fn() { 5 + 10; }; fivePlusTen();",
                        15
                ),
                new VMTestCase(
                        "let one = fn() { 1; }; let two = fn() { 2; }; one() + two();",
                        3
                ),
                new VMTestCase(
                        "let a = fn() { 1 }; let b = fn() { a() + 1 }; let c = fn() { b() + 1 }; c();",
                        3
                )
        };
        runVMTests(tests);
    }

    /**
     * Chapter 7: 測試返回語句
     */
    @Test
    public void testFunctionsWithReturnStatement() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase(
                        "let earlyExit = fn() { return 99; 100; }; earlyExit();",
                        99
                ),
                new VMTestCase(
                        "let earlyExit = fn() { return 99; return 100; }; earlyExit();",
                        99
                )
        };
        runVMTests(tests);
    }

    /**
     * Chapter 7: 測試無返回值函數
     */
    @Test
    public void testFunctionsWithoutReturnValue() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase(
                        "let noReturn = fn() { }; noReturn();",
                        new NullObject()
                ),
                new VMTestCase(
                        "let noReturn = fn() { }; let noReturnTwo = fn() { noReturn(); }; noReturn(); noReturnTwo();",
                        new NullObject()
                )
        };
        runVMTests(tests);
    }

    /**
     * Chapter 7: 測試一級函數
     */
    @Test
    public void testFirstClassFunctions() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase(
                        "let returnsOne = fn() { 1; }; let returnsOneReturner = fn() { returnsOne; }; returnsOneReturner()();",
                        1
                )
        };
        runVMTests(tests);
    }

    /**
     * Chapter 7: 測試局部綁定
     */
    @Test
    public void testCallingFunctionsWithBindings() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase(
                        "let one = fn() { let one = 1; one }; one();",
                        1
                ),
                new VMTestCase(
                        "let oneAndTwo = fn() { let one = 1; let two = 2; one + two; }; oneAndTwo();",
                        3
                ),
                new VMTestCase(
                        "let oneAndTwo = fn() { let one = 1; let two = 2; one + two; }; let threeAndFour = fn() { let three = 3; let four = 4; three + four; }; oneAndTwo() + threeAndFour();",
                        10
                ),
                new VMTestCase(
                        "let firstFoobar = fn() { let foobar = 50; foobar; }; let secondFoobar = fn() { let foobar = 100; foobar; }; firstFoobar() + secondFoobar();",
                        150
                ),
                new VMTestCase(
                        "let globalSeed = 50; let minusOne = fn() { let num = 1; globalSeed - num; }; let minusTwo = fn() { let num = 2; globalSeed - num; }; minusOne() + minusTwo();",
                        97
                )
        };
        runVMTests(tests);
    }

    /**
     * Chapter 7: 測試帶參數的函數調用
     */
    @Test
    public void testCallingFunctionsWithArgumentsAndBindings() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase(
                        "let identity = fn(a) { a; }; identity(4);",
                        4
                ),
                new VMTestCase(
                        "let sum = fn(a, b) { a + b; }; sum(1, 2);",
                        3
                ),
                new VMTestCase(
                        "let sum = fn(a, b) { let c = a + b; c; }; sum(1, 2);",
                        3
                ),
                new VMTestCase(
                        "let sum = fn(a, b) { let c = a + b; c; }; sum(1, 2) + sum(3, 4);",
                        10
                ),
                new VMTestCase(
                        "let sum = fn(a, b) { let c = a + b; c; }; let outer = fn() { sum(1, 2) + sum(3, 4); }; outer();",
                        10
                ),
                new VMTestCase(
                        "let globalNum = 10; let sum = fn(a, b) { let c = a + b; c + globalNum; }; let outer = fn() { sum(1, 2) + sum(3, 4) + globalNum; }; outer() + globalNum;",
                        50
                )
        };
        runVMTests(tests);
    }

    /**
     * Chapter 7: 測試錯誤的參數數量
     */
    @Test
    public void testCallingFunctionsWithWrongArguments() {
        VMTestCase[] tests = new VMTestCase[]{
                new VMTestCase(
                        "fn() { 1; }(1);",
                        "wrong number of arguments: want=0, got=1"
                ),
                new VMTestCase(
                        "fn(a) { a; }();",
                        "wrong number of arguments: want=1, got=0"
                ),
                new VMTestCase(
                        "fn(a, b) { a + b; }(1);",
                        "wrong number of arguments: want=2, got=1"
                )
        };
        runVMErrorTests(tests);
    }

    private void runVMTests(VMTestCase[] tests) {
        for (VMTestCase tt : tests) {
            System.out.println("Testing: " + tt.input);  // 添加這行

            Program program = parse(tt.input);

            Compiler comp = new Compiler();
            try {
                comp.compile(program);
            } catch (Compiler.CompilerException e) {
                fail("compiler error: " + e.getMessage());
            }

            // 打印編譯結果
            System.out.println("Instructions:\n" + comp.bytecode().getInstructions());
            System.out.println("Constants: " + comp.bytecode().getConstants().size());

            VM vm = new VM(comp.bytecode());
            try {
                vm.run();
            } catch (VM.VMException e) {
                fail("vm error: " + e.getMessage());
            }

            MonkeyObject stackElem = vm.lastPoppedStackElem();
            System.out.println("Stack elem: " + stackElem);  // 添加這行

            testExpectedObject(tt.expected, stackElem);
        }
    }

    private void runVMErrorTests(VMTestCase[] tests) {
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
                fail("expected VM error but resulted in none.");
            } catch (VM.VMException e) {
                assertEquals(tt.expected, e.getMessage(),
                        "wrong VM error");
            }
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
        } else if (expected instanceof String) {
            testStringObject((String) expected, actual);
        } else if (expected instanceof int[]) {
            testArrayObject((int[]) expected, actual);
        } else if (expected instanceof Map) {
            testHashObject((Map<HashKey, Integer>) expected, actual);
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

    private void testStringObject(String expected, MonkeyObject actual) {
        assertTrue(actual instanceof StringObject,
                "object is not String. got=" + actual.getClass());

        StringObject result = (StringObject) actual;
        assertEquals(expected, result.getValue(),
                String.format("object has wrong value. got=%s, want=%s",
                        result.getValue(), expected));
    }

    private void testArrayObject(int[] expected, MonkeyObject actual) {
        assertTrue(actual instanceof ArrayObject,
                "object not Array: " + actual.getClass());

        ArrayObject array = (ArrayObject) actual;
        assertEquals(expected.length, array.getElements().size(),
                String.format("wrong num of elements. want=%d, got=%d",
                        expected.length, array.getElements().size()));

        for (int i = 0; i < expected.length; i++) {
            testIntegerObject(expected[i], array.getElements().get(i));
        }
    }

    private void testHashObject(Map<HashKey, Integer> expected, MonkeyObject actual) {
        assertTrue(actual instanceof HashObject,
                "object is not Hash. got=" + actual.getClass());

        HashObject hash = (HashObject) actual;
        assertEquals(expected.size(), hash.getPairs().size(),
                String.format("hash has wrong number of Pairs. want=%d, got=%d",
                        expected.size(), hash.getPairs().size()));

        for (Map.Entry<HashKey, Integer> entry : expected.entrySet()) {
            HashObject.HashPair pair = hash.getPairs().get(entry.getKey());
            assertNotNull(pair, "no pair for given key in Pairs");

            testIntegerObject(entry.getValue(), pair.value);
        }
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