package com.monkey.parser;

import com.monkey.ast.*;
import com.monkey.lexer.Lexer;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Parser 測試類 - 第二章完整測試
 */
public class ParserTest {

    private void checkParserErrors(Parser p) {
        var errors = p.getErrors();
        if (errors.isEmpty()) {
            return;
        }

        System.err.println("parser has " + errors.size() + " errors");
        for (String msg : errors) {
            System.err.println("parser error: " + msg);
        }
        fail("parser errors found");
    }

    @Test
    public void testLetStatements() {
        String input = """
            let x = 5;
            let y = 10;
            let foobar = 838383;
            """;

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);

        Program program = p.parseProgram();
        checkParserErrors(p);

        assertNotNull(program);
        assertEquals(3, program.getStatements().size());

        String[] expectedIdentifiers = {"x", "y", "foobar"};

        for (int i = 0; i < expectedIdentifiers.length; i++) {
            Statement stmt = program.getStatements().get(i);
            testLetStatement(stmt, expectedIdentifiers[i]);
        }
    }

    private void testLetStatement(Statement s, String name) {
        assertEquals("let", s.tokenLiteral());
        assertTrue(s instanceof LetStatement);

        LetStatement letStmt = (LetStatement) s;
        assertEquals(name, letStmt.getName().getValue());
        assertEquals(name, letStmt.getName().tokenLiteral());
    }

    @Test
    public void testReturnStatements() {
        String input = """
            return 5;
            return 10;
            return 993322;
            """;

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);

        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(3, program.getStatements().size());

        for (Statement stmt : program.getStatements()) {
            assertTrue(stmt instanceof ReturnStatement);
            assertEquals("return", stmt.tokenLiteral());
        }
    }

    @Test
    public void testIdentifierExpression() {
        String input = "foobar;";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue(exprStmt.getExpression() instanceof Identifier);

        Identifier ident = (Identifier) exprStmt.getExpression();
        assertEquals("foobar", ident.getValue());
        assertEquals("foobar", ident.tokenLiteral());
    }

    @Test
    public void testIntegerLiteralExpression() {
        String input = "5;";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue(exprStmt.getExpression() instanceof IntegerLiteral);

        IntegerLiteral literal = (IntegerLiteral) exprStmt.getExpression();
        assertEquals(5, literal.getValue());
        assertEquals("5", literal.tokenLiteral());
    }

    @Test
    public void testParsingPrefixExpressions() {
        class PrefixTest {
            String input;
            String operator;
            Object value;

            PrefixTest(String input, String operator, Object value) {
                this.input = input;
                this.operator = operator;
                this.value = value;
            }
        }

        PrefixTest[] tests = {
                new PrefixTest("!5;", "!", 5L),
                new PrefixTest("-15;", "-", 15L),
                new PrefixTest("!true;", "!", true),
                new PrefixTest("!false;", "!", false)
        };

        for (PrefixTest tt : tests) {
            Lexer l = new Lexer(tt.input);
            Parser p = new Parser(l);
            Program program = p.parseProgram();
            checkParserErrors(p);

            assertEquals(1, program.getStatements().size());

            Statement stmt = program.getStatements().get(0);
            assertTrue(stmt instanceof ExpressionStatement);

            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            assertTrue(exprStmt.getExpression() instanceof PrefixExpression);

            PrefixExpression exp = (PrefixExpression) exprStmt.getExpression();
            assertEquals(tt.operator, exp.getOperator());
            testLiteralExpression(exp.getRight(), tt.value);
        }
    }

    @Test
    public void testParsingInfixExpressions() {
        class InfixTest {
            String input;
            Object leftValue;
            String operator;
            Object rightValue;

            InfixTest(String input, Object leftValue, String operator, Object rightValue) {
                this.input = input;
                this.leftValue = leftValue;
                this.operator = operator;
                this.rightValue = rightValue;
            }
        }

        InfixTest[] tests = {
                new InfixTest("5 + 5;", 5L, "+", 5L),
                new InfixTest("5 - 5;", 5L, "-", 5L),
                new InfixTest("5 * 5;", 5L, "*", 5L),
                new InfixTest("5 / 5;", 5L, "/", 5L),
                new InfixTest("5 > 5;", 5L, ">", 5L),
                new InfixTest("5 < 5;", 5L, "<", 5L),
                new InfixTest("5 == 5;", 5L, "==", 5L),
                new InfixTest("5 != 5;", 5L, "!=", 5L),
                new InfixTest("true == true", true, "==", true),
                new InfixTest("true != false", true, "!=", false),
                new InfixTest("false == false", false, "==", false)
        };

        for (InfixTest tt : tests) {
            Lexer l = new Lexer(tt.input);
            Parser p = new Parser(l);
            Program program = p.parseProgram();
            checkParserErrors(p);

            assertEquals(1, program.getStatements().size());

            Statement stmt = program.getStatements().get(0);
            assertTrue(stmt instanceof ExpressionStatement);

            ExpressionStatement exprStmt = (ExpressionStatement) stmt;
            testInfixExpression(exprStmt.getExpression(), tt.leftValue, tt.operator, tt.rightValue);
        }
    }

    @Test
    public void testOperatorPrecedenceParsing() {
        class PrecedenceTest {
            String input;
            String expected;

            PrecedenceTest(String input, String expected) {
                this.input = input;
                this.expected = expected;
            }
        }

        PrecedenceTest[] tests = {
                new PrecedenceTest("-a * b", "((-a) * b)"),
                new PrecedenceTest("!-a", "(!(-a))"),
                new PrecedenceTest("a + b + c", "((a + b) + c)"),
                new PrecedenceTest("a + b - c", "((a + b) - c)"),
                new PrecedenceTest("a * b * c", "((a * b) * c)"),
                new PrecedenceTest("a * b / c", "((a * b) / c)"),
                new PrecedenceTest("a + b / c", "(a + (b / c))"),
                new PrecedenceTest("a + b * c + d / e - f", "(((a + (b * c)) + (d / e)) - f)"),
                new PrecedenceTest("3 + 4; -5 * 5", "(3 + 4)((-5) * 5)"),
                new PrecedenceTest("5 > 4 == 3 < 4", "((5 > 4) == (3 < 4))"),
                new PrecedenceTest("5 < 4 != 3 > 4", "((5 < 4) != (3 > 4))"),
                new PrecedenceTest("3 + 4 * 5 == 3 * 1 + 4 * 5", "((3 + (4 * 5)) == ((3 * 1) + (4 * 5)))"),
                new PrecedenceTest("true", "true"),
                new PrecedenceTest("false", "false"),
                new PrecedenceTest("3 > 5 == false", "((3 > 5) == false)"),
                new PrecedenceTest("3 < 5 == true", "((3 < 5) == true)"),
                new PrecedenceTest("1 + (2 + 3) + 4", "((1 + (2 + 3)) + 4)"),
                new PrecedenceTest("(5 + 5) * 2", "((5 + 5) * 2)"),
                new PrecedenceTest("2 / (5 + 5)", "(2 / (5 + 5))"),
                new PrecedenceTest("-(5 + 5)", "(-(5 + 5))"),
                new PrecedenceTest("!(true == true)", "(!(true == true))"),
                new PrecedenceTest("a + add(b * c) + d", "((a + add((b * c))) + d)"),
                new PrecedenceTest("add(a, b, 1, 2 * 3, 4 + 5, add(6, 7 * 8))",
                        "add(a, b, 1, (2 * 3), (4 + 5), add(6, (7 * 8)))"),
                new PrecedenceTest("add(a + b + c * d / f + g)", "add((((a + b) + ((c * d) / f)) + g))")
        };

        for (PrecedenceTest tt : tests) {
            Lexer l = new Lexer(tt.input);
            Parser p = new Parser(l);
            Program program = p.parseProgram();
            checkParserErrors(p);

            String actual = program.string();
            assertEquals(tt.expected, actual,
                    "For input: " + tt.input);
        }
    }

    @Test
    public void testBooleanExpression() {
        String input = "true;";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue(exprStmt.getExpression() instanceof BooleanLiteral);

        BooleanLiteral bool = (BooleanLiteral) exprStmt.getExpression();
        assertTrue(bool.getValue());
    }

    @Test
    public void testIfExpression() {
        String input = "if (x < y) { x }";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue(exprStmt.getExpression() instanceof IfExpression);

        IfExpression exp = (IfExpression) exprStmt.getExpression();
        testInfixExpression(exp.getCondition(), "x", "<", "y");

        assertEquals(1, exp.getConsequence().getStatements().size());

        Statement consequence = exp.getConsequence().getStatements().get(0);
        assertTrue(consequence instanceof ExpressionStatement);

        testIdentifier(((ExpressionStatement) consequence).getExpression(), "x");
        assertNull(exp.getAlternative());
    }

    @Test
    public void testIfElseExpression() {
        String input = "if (x < y) { x } else { y }";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue(exprStmt.getExpression() instanceof IfExpression);

        IfExpression exp = (IfExpression) exprStmt.getExpression();
        testInfixExpression(exp.getCondition(), "x", "<", "y");

        assertEquals(1, exp.getConsequence().getStatements().size());
        Statement consequence = exp.getConsequence().getStatements().get(0);
        assertTrue(consequence instanceof ExpressionStatement);
        testIdentifier(((ExpressionStatement) consequence).getExpression(), "x");

        assertNotNull(exp.getAlternative());
        assertEquals(1, exp.getAlternative().getStatements().size());
        Statement alternative = exp.getAlternative().getStatements().get(0);
        assertTrue(alternative instanceof ExpressionStatement);
        testIdentifier(((ExpressionStatement) alternative).getExpression(), "y");
    }

    @Test
    public void testFunctionLiteralParsing() {
        String input = "fn(x, y) { x + y; }";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue(exprStmt.getExpression() instanceof FunctionLiteral);

        FunctionLiteral function = (FunctionLiteral) exprStmt.getExpression();
        assertEquals(2, function.getParameters().size());

        testLiteralExpression(function.getParameters().get(0), "x");
        testLiteralExpression(function.getParameters().get(1), "y");

        assertEquals(1, function.getBody().getStatements().size());

        Statement bodyStmt = function.getBody().getStatements().get(0);
        assertTrue(bodyStmt instanceof ExpressionStatement);

        testInfixExpression(((ExpressionStatement) bodyStmt).getExpression(),
                "x", "+", "y");
    }

    @Test
    public void testFunctionParameterParsing() {
        class FunctionTest {
            String input;
            String[] expectedParams;

            FunctionTest(String input, String[] expectedParams) {
                this.input = input;
                this.expectedParams = expectedParams;
            }
        }

        FunctionTest[] tests = {
                new FunctionTest("fn() {};", new String[]{}),
                new FunctionTest("fn(x) {};", new String[]{"x"}),
                new FunctionTest("fn(x, y, z) {};", new String[]{"x", "y", "z"})
        };

        for (FunctionTest tt : tests) {
            Lexer l = new Lexer(tt.input);
            Parser p = new Parser(l);
            Program program = p.parseProgram();
            checkParserErrors(p);

            Statement stmt = program.getStatements().get(0);
            FunctionLiteral function = (FunctionLiteral)
                    ((ExpressionStatement) stmt).getExpression();

            assertEquals(tt.expectedParams.length, function.getParameters().size());

            for (int i = 0; i < tt.expectedParams.length; i++) {
                testLiteralExpression(function.getParameters().get(i), tt.expectedParams[i]);
            }
        }
    }

    @Test
    public void testCallExpressionParsing() {
        String input = "add(1, 2 * 3, 4 + 5);";

        Lexer l = new Lexer(input);
        Parser p = new Parser(l);
        Program program = p.parseProgram();
        checkParserErrors(p);

        assertEquals(1, program.getStatements().size());

        Statement stmt = program.getStatements().get(0);
        assertTrue(stmt instanceof ExpressionStatement);

        ExpressionStatement exprStmt = (ExpressionStatement) stmt;
        assertTrue(exprStmt.getExpression() instanceof CallExpression);

        CallExpression exp = (CallExpression) exprStmt.getExpression();
        testIdentifier(exp.getFunction(), "add");

        assertEquals(3, exp.getArguments().size());

        testLiteralExpression(exp.getArguments().get(0), 1L);
        testInfixExpression(exp.getArguments().get(1), 2L, "*", 3L);
        testInfixExpression(exp.getArguments().get(2), 4L, "+", 5L);
    }

    // 輔助測試方法

    private void testLiteralExpression(Expression exp, Object expected) {
        if (expected instanceof Long) {
            testIntegerLiteral(exp, (Long) expected);
        } else if (expected instanceof String) {
            testIdentifier(exp, (String) expected);
        } else if (expected instanceof Boolean) {
            testBooleanLiteral(exp, (Boolean) expected);
        } else {
            fail("type of exp not handled. got=" + exp.getClass().getName());
        }
    }

    private void testIntegerLiteral(Expression il, long value) {
        assertTrue(il instanceof IntegerLiteral);
        IntegerLiteral integ = (IntegerLiteral) il;
        assertEquals(value, integ.getValue());
        assertEquals(String.valueOf(value), integ.tokenLiteral());
    }

    private void testIdentifier(Expression exp, String value) {
        assertTrue(exp instanceof Identifier);
        Identifier ident = (Identifier) exp;
        assertEquals(value, ident.getValue());
        assertEquals(value, ident.tokenLiteral());
    }

    private void testBooleanLiteral(Expression exp, boolean value) {
        assertTrue(exp instanceof BooleanLiteral);
        BooleanLiteral bool = (BooleanLiteral) exp;
        assertEquals(value, bool.getValue());
        assertEquals(String.valueOf(value), bool.tokenLiteral());
    }

    private void testInfixExpression(Expression exp, Object left, String operator, Object right) {
        assertTrue(exp instanceof InfixExpression);
        InfixExpression opExp = (InfixExpression) exp;

        testLiteralExpression(opExp.getLeft(), left);
        assertEquals(operator, opExp.getOperator());
        testLiteralExpression(opExp.getRight(), right);
    }
}