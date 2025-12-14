package com.monkey.evaluator;

import com.monkey.ast.Program;
import com.monkey.lexer.Lexer;
import com.monkey.object.*;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 第四章測試 - 字串、陣列、雜湊、內建函數
 */
public class Chapter4Test {

    private MonkeyObject testEval(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        Environment env = new Environment();
        return Evaluator.eval(program, env);
    }

    @Test
    public void testStringLiteral() {
        String input = "\"Hello World!\"";
        MonkeyObject evaluated = testEval(input);

        assertTrue(evaluated instanceof StringObject);
        assertEquals("Hello World!", ((StringObject) evaluated).getValue());
    }

    @Test
    public void testStringConcatenation() {
        String input = "\"Hello\" + \" \" + \"World!\"";
        MonkeyObject evaluated = testEval(input);

        assertTrue(evaluated instanceof StringObject);
        assertEquals("Hello World!", ((StringObject) evaluated).getValue());
    }

    @Test
    public void testBuiltinFunctions() {
        Object[][] tests = {
                {"len(\"\")", 0L},
                {"len(\"four\")", 4L},
                {"len(\"hello world\")", 11L},
                {"len([1, 2, 3])", 3L},
                {"len([])", 0L},
                {"first([1, 2, 3])", 1L},
                {"last([1, 2, 3])", 3L},
                {"len(1)", "argument to `len` not supported, got INTEGER"},
                {"len(\"one\", \"two\")", "wrong number of arguments. got=2, want=1"}
        };

        for (Object[] tt : tests) {
            MonkeyObject evaluated = testEval((String) tt[0]);

            if (tt[1] instanceof Long) {
                testIntegerObject(evaluated, (Long) tt[1]);
            } else if (tt[1] instanceof String) {
                assertTrue(evaluated instanceof ErrorObject);
                assertEquals(tt[1], ((ErrorObject) evaluated).getMessage());
            }
        }
    }

    @Test
    public void testArrayLiterals() {
        String input = "[1, 2 * 2, 3 + 3]";
        MonkeyObject evaluated = testEval(input);

        assertTrue(evaluated instanceof ArrayObject);
        ArrayObject result = (ArrayObject) evaluated;

        assertEquals(3, result.getElements().size());
        testIntegerObject(result.getElements().get(0), 1L);
        testIntegerObject(result.getElements().get(1), 4L);
        testIntegerObject(result.getElements().get(2), 6L);
    }

    @Test
    public void testArrayIndexExpressions() {
        Object[][] tests = {
                {"[1, 2, 3][0]", 1L},
                {"[1, 2, 3][1]", 2L},
                {"[1, 2, 3][2]", 3L},
                {"let i = 0; [1][i];", 1L},
                {"[1, 2, 3][1 + 1];", 3L},
                {"let myArray = [1, 2, 3]; myArray[2];", 3L},
                {"let myArray = [1, 2, 3]; myArray[0] + myArray[1] + myArray[2];", 6L},
                {"let myArray = [1, 2, 3]; let i = myArray[0]; myArray[i]", 2L},
                {"[1, 2, 3][3]", null},
                {"[1, 2, 3][-1]", null}
        };

        for (Object[] tt : tests) {
            MonkeyObject evaluated = testEval((String) tt[0]);
            if (tt[1] instanceof Long) {
                testIntegerObject(evaluated, (Long) tt[1]);
            } else {
                testNullObject(evaluated);
            }
        }
    }

    @Test
    public void testHashLiterals() {
        String input = """
            let two = "two";
            {
                "one": 10 - 9,
                two: 1 + 1,
                "thr" + "ee": 6 / 2,
                4: 4,
                true: 5,
                false: 6
            }
            """;

        MonkeyObject evaluated = testEval(input);
        assertTrue(evaluated instanceof HashObject);

        HashObject result = (HashObject) evaluated;

        Map<HashKey, Long> expected = Map.of(
                new StringObject("one").hashKey(), 1L,
                new StringObject("two").hashKey(), 2L,
                new StringObject("three").hashKey(), 3L,
                new IntegerObject(4).hashKey(), 4L,
                BooleanObject.TRUE.hashKey(), 5L,
                BooleanObject.FALSE.hashKey(), 6L
        );

        assertEquals(expected.size(), result.getPairs().size());

        for (Map.Entry<HashKey, Long> entry : expected.entrySet()) {
            HashObject.HashPair pair = result.getPairs().get(entry.getKey());
            assertNotNull(pair);
            testIntegerObject(pair.value, entry.getValue());
        }
    }

    @Test
    public void testHashIndexExpressions() {
        Object[][] tests = {
                {"{\"foo\": 5}[\"foo\"]", 5L},
                {"{\"foo\": 5}[\"bar\"]", null},
                {"let key = \"foo\"; {\"foo\": 5}[key]", 5L},
                {"{}[\"foo\"]", null},
                {"{5: 5}[5]", 5L},
                {"{true: 5}[true]", 5L},
                {"{false: 5}[false]", 5L}
        };

        for (Object[] tt : tests) {
            MonkeyObject evaluated = testEval((String) tt[0]);
            if (tt[1] instanceof Long) {
                testIntegerObject(evaluated, (Long) tt[1]);
            } else {
                testNullObject(evaluated);
            }
        }
    }

    @Test
    public void testArrayBuiltinFunctions() {
        Object[][] tests = {
                {"first([1, 2, 3])", 1L},
                {"last([1, 2, 3])", 3L},
                {"first([])", null},
                {"last([])", null},
                {"let a = [1, 2, 3]; rest(a)", new long[]{2, 3}},
                {"let a = [1]; rest(a)", new long[]{}},
                {"rest([])", null},
                {"let a = [1, 2]; push(a, 3)", new long[]{1, 2, 3}},
                {"let a = []; push(a, 1)", new long[]{1}}
        };

        for (Object[] tt : tests) {
            MonkeyObject evaluated = testEval((String) tt[0]);

            if (tt[1] instanceof Long) {
                testIntegerObject(evaluated, (Long) tt[1]);
            } else if (tt[1] instanceof long[]) {
                assertTrue(evaluated instanceof ArrayObject);
                ArrayObject arr = (ArrayObject) evaluated;
                long[] expected = (long[]) tt[1];
                assertEquals(expected.length, arr.getElements().size());
                for (int i = 0; i < expected.length; i++) {
                    testIntegerObject(arr.getElements().get(i), expected[i]);
                }
            } else {
                testNullObject(evaluated);
            }
        }
    }

    // 輔助方法

    private void testIntegerObject(MonkeyObject obj, long expected) {
        assertTrue(obj instanceof IntegerObject);
        assertEquals(expected, ((IntegerObject) obj).getValue());
    }

    private void testNullObject(MonkeyObject obj) {
        assertEquals(NullObject.NULL, obj);
    }
}
