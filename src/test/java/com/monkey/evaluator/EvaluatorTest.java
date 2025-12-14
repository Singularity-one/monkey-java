package com.monkey.evaluator;

import com.monkey.ast.Program;
import com.monkey.lexer.Lexer;
import com.monkey.object.*;
import com.monkey.parser.Parser;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Evaluator 測試類 - 第三章完整測試
 */
public class EvaluatorTest {

    private MonkeyObject testEval(String input) {
        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        Environment env = new Environment();
        return Evaluator.eval(program, env);
    }

    @Test
    public void testEvalIntegerExpression() {
        Object[][] tests = {
                {"5", 5L},
                {"10", 10L},
                {"-5", -5L},
                {"-10", -10L},
                {"5 + 5 + 5 + 5 - 10", 10L},
                {"2 * 2 * 2 * 2 * 2", 32L},
                {"-50 + 100 + -50", 0L},
                {"5 * 2 + 10", 20L},
                {"5 + 2 * 10", 25L},
                {"20 + 2 * -10", 0L},
                {"50 / 2 * 2 + 10", 60L},
                {"2 * (5 + 10)", 30L},
                {"3 * 3 * 3 + 10", 37L},
                {"3 * (3 * 3) + 10", 37L},
                {"(5 + 10 * 2 + 15 / 3) * 2 + -10", 50L}
        };

        for (Object[] tt : tests) {
            MonkeyObject evaluated = testEval((String) tt[0]);
            testIntegerObject(evaluated, (Long) tt[1]);
        }
    }

    @Test
    public void testEvalBooleanExpression() {
        Object[][] tests = {
                {"true", true},
                {"false", false},
                {"1 < 2", true},
                {"1 > 2", false},
                {"1 == 1", true},
                {"1 != 1", false},
                {"true == true", true},
                {"false == false", true},
                {"true == false", false},
                {"(1 < 2) == true", true}
        };

        for (Object[] tt : tests) {
            testBooleanObject(testEval((String) tt[0]), (Boolean) tt[1]);
        }
    }

    @Test
    public void testBangOperator() {
        Object[][] tests = {
                {"!true", false},
                {"!false", true},
                {"!5", false},
                {"!!true", true},
                {"!!false", false},
                {"!!5", true}
        };

        for (Object[] tt : tests) {
            testBooleanObject(testEval((String) tt[0]), (Boolean) tt[1]);
        }
    }

    @Test
    public void testIfElseExpressions() {
        Object[][] tests = {
                {"if (true) { 10 }", 10L},
                {"if (false) { 10 }", null},
                {"if (1 < 2) { 10 }", 10L},
                {"if (1 > 2) { 10 }", null},
                {"if (1 > 2) { 10 } else { 20 }", 20L},
                {"if (1 < 2) { 10 } else { 20 }", 10L}
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
    public void testReturnStatements() {
        Object[][] tests = {
                {"return 10;", 10L},
                {"return 10; 9;", 10L},
                {"return 2 * 5; 9;", 10L},
                {"9; return 2 * 5; 9;", 10L},
                {"if (10 > 1) { if (10 > 1) { return 10; } return 1; }", 10L}
        };

        for (Object[] tt : tests) {
            testIntegerObject(testEval((String) tt[0]), (Long) tt[1]);
        }
    }

    @Test
    public void testErrorHandling() {
        Object[][] tests = {
                {"5 + true;", "type mismatch: INTEGER + BOOLEAN"},
                {"-true", "unknown operator: -BOOLEAN"},
                {"true + false;", "unknown operator: BOOLEAN + BOOLEAN"},
                {"foobar", "identifier not found: foobar"},
                {"10 / 0", "division by zero"}
        };

        for (Object[] tt : tests) {
            MonkeyObject evaluated = testEval((String) tt[0]);
            assertTrue(evaluated instanceof ErrorObject);
            assertEquals(tt[1], ((ErrorObject) evaluated).getMessage());
        }
    }

    @Test
    public void testLetStatements() {
        Object[][] tests = {
                {"let a = 5; a;", 5L},
                {"let a = 5 * 5; a;", 25L},
                {"let a = 5; let b = a; b;", 5L},
                {"let a = 5; let b = a; let c = a + b + 5; c;", 15L}
        };

        for (Object[] tt : tests) {
            testIntegerObject(testEval((String) tt[0]), (Long) tt[1]);
        }
    }

    @Test
    public void testFunctionObject() {
        String input = "fn(x) { x + 2; };";
        MonkeyObject evaluated = testEval(input);
        assertTrue(evaluated instanceof FunctionObject);

        FunctionObject fn = (FunctionObject) evaluated;
        assertEquals(1, fn.getParameters().size());
        assertEquals("x", fn.getParameters().get(0).string());
        assertEquals("(x + 2)", fn.getBody().string());
    }

    @Test
    public void testFunctionApplication() {
        Object[][] tests = {
                {"let identity = fn(x) { x; }; identity(5);", 5L},
                {"let identity = fn(x) { return x; }; identity(5);", 5L},
                {"let double = fn(x) { x * 2; }; double(5);", 10L},
                {"let add = fn(x, y) { x + y; }; add(5, 5);", 10L},
                {"let add = fn(x, y) { x + y; }; add(5 + 5, add(5, 5));", 20L},
                {"fn(x) { x; }(5)", 5L}
        };

        for (Object[] tt : tests) {
            testIntegerObject(testEval((String) tt[0]), (Long) tt[1]);
        }
    }

    @Test
    public void testClosures() {
        String input = """
            let newAdder = fn(x) {
                fn(y) { x + y };
            };
            let addTwo = newAdder(2);
            addTwo(2);
            """;

        testIntegerObject(testEval(input), 4L);
    }

    // 輔助測試方法

    private void testIntegerObject(MonkeyObject obj, long expected) {
        assertTrue(obj instanceof IntegerObject,
                "object is not Integer. got=" + obj.getClass().getName());

        IntegerObject result = (IntegerObject) obj;
        assertEquals(expected, result.getValue(),
                "object has wrong value. got=" + result.getValue() + ", want=" + expected);
    }

    private void testBooleanObject(MonkeyObject obj, boolean expected) {
        assertTrue(obj instanceof BooleanObject,
                "object is not Boolean. got=" + obj.getClass().getName());

        BooleanObject result = (BooleanObject) obj;
        assertEquals(expected, result.getValue(),
                "object has wrong value. got=" + result.getValue() + ", want=" + expected);
    }

    private void testNullObject(MonkeyObject obj) {
        assertEquals(NullObject.NULL, obj,
                "object is not NULL. got=" + obj);
    }
}