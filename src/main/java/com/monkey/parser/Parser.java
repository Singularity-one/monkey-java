package com.monkey.parser;

import com.monkey.ast.*;
import com.monkey.lexer.Lexer;
import com.monkey.token.Token;
import com.monkey.token.TokenType;

import java.util.*;
import java.util.function.Function;

/**
 * Parser 實現 Pratt Parsing（Top Down Operator Precedence）
 */
public class Parser {
    private final Lexer lexer;
    private Token curToken;
    private Token peekToken;
    private final List<String> errors;

    // 運算符優先級
    private static final int LOWEST = 1;
    private static final int EQUALS = 2;      // ==
    private static final int LESSGREATER = 3; // > or <
    private static final int SUM = 4;         // +
    private static final int PRODUCT = 5;     // *
    private static final int PREFIX = 6;      // -X or !X
    private static final int CALL = 7;        // myFunction(X)

    // 優先級映射表
    private static final Map<TokenType, Integer> PRECEDENCES = new HashMap<>();
    static {
        PRECEDENCES.put(TokenType.EQ, EQUALS);
        PRECEDENCES.put(TokenType.NOT_EQ, EQUALS);
        PRECEDENCES.put(TokenType.LT, LESSGREATER);
        PRECEDENCES.put(TokenType.GT, LESSGREATER);
        PRECEDENCES.put(TokenType.PLUS, SUM);
        PRECEDENCES.put(TokenType.MINUS, SUM);
        PRECEDENCES.put(TokenType.SLASH, PRODUCT);
        PRECEDENCES.put(TokenType.ASTERISK, PRODUCT);
        PRECEDENCES.put(TokenType.LPAREN, CALL);
    }

    // 前綴解析函數映射
    private final Map<TokenType, Function<Void, Expression>> prefixParseFns;

    // 中綴解析函數映射
    private final Map<TokenType, Function<Expression, Expression>> infixParseFns;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.errors = new ArrayList<>();
        this.prefixParseFns = new HashMap<>();
        this.infixParseFns = new HashMap<>();

        // 註冊前綴解析函數
        registerPrefix(TokenType.IDENT, v -> parseIdentifier());
        registerPrefix(TokenType.INT, v -> parseIntegerLiteral());
        registerPrefix(TokenType.BANG, v -> parsePrefixExpression());
        registerPrefix(TokenType.MINUS, v -> parsePrefixExpression());
        registerPrefix(TokenType.TRUE, v -> parseBoolean());
        registerPrefix(TokenType.FALSE, v -> parseBoolean());
        registerPrefix(TokenType.LPAREN, v -> parseGroupedExpression());
        registerPrefix(TokenType.IF, v -> parseIfExpression());
        registerPrefix(TokenType.FUNCTION, v -> parseFunctionLiteral());

        // 註冊中綴解析函數
        registerInfix(TokenType.PLUS, this::parseInfixExpression);
        registerInfix(TokenType.MINUS, this::parseInfixExpression);
        registerInfix(TokenType.SLASH, this::parseInfixExpression);
        registerInfix(TokenType.ASTERISK, this::parseInfixExpression);
        registerInfix(TokenType.EQ, this::parseInfixExpression);
        registerInfix(TokenType.NOT_EQ, this::parseInfixExpression);
        registerInfix(TokenType.LT, this::parseInfixExpression);
        registerInfix(TokenType.GT, this::parseInfixExpression);
        registerInfix(TokenType.LPAREN, this::parseCallExpression);

        // 讀取兩個 token，設置 curToken 和 peekToken
        nextToken();
        nextToken();
    }

    private void registerPrefix(TokenType tokenType, Function<Void, Expression> fn) {
        prefixParseFns.put(tokenType, fn);
    }

    private void registerInfix(TokenType tokenType, Function<Expression, Expression> fn) {
        infixParseFns.put(tokenType, fn);
    }

    private void nextToken() {
        curToken = peekToken;
        peekToken = lexer.nextToken();
    }

    private boolean curTokenIs(TokenType t) {
        return curToken.getType() == t;
    }

    private boolean peekTokenIs(TokenType t) {
        return peekToken.getType() == t;
    }

    private boolean expectPeek(TokenType t) {
        if (peekTokenIs(t)) {
            nextToken();
            return true;
        } else {
            peekError(t);
            return false;
        }
    }

    private void peekError(TokenType t) {
        String msg = String.format("expected next token to be %s, got %s instead",
                t, peekToken.getType());
        errors.add(msg);
    }

    private void noPrefixParseFnError(TokenType t) {
        String msg = String.format("no prefix parse function for %s found", t);
        errors.add(msg);
    }

    private int peekPrecedence() {
        return PRECEDENCES.getOrDefault(peekToken.getType(), LOWEST);
    }

    private int curPrecedence() {
        return PRECEDENCES.getOrDefault(curToken.getType(), LOWEST);
    }

    public List<String> getErrors() {
        return errors;
    }

    /**
     * 解析程式，返回 AST
     */
    public Program parseProgram() {
        Program program = new Program();

        while (!curTokenIs(TokenType.EOF)) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                program.addStatement(stmt);
            }
            nextToken();
        }

        return program;
    }

    private Statement parseStatement() {
        return switch (curToken.getType()) {
            case LET -> parseLetStatement();
            case RETURN -> parseReturnStatement();
            default -> parseExpressionStatement();
        };
    }

    private LetStatement parseLetStatement() {
        LetStatement stmt = new LetStatement(curToken);

        if (!expectPeek(TokenType.IDENT)) {
            return null;
        }

        stmt.setName(new Identifier(curToken, curToken.getLiteral()));

        if (!expectPeek(TokenType.ASSIGN)) {
            return null;
        }

        nextToken();

        stmt.setValue(parseExpression(LOWEST));

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    private ReturnStatement parseReturnStatement() {
        ReturnStatement stmt = new ReturnStatement(curToken);

        nextToken();

        stmt.setReturnValue(parseExpression(LOWEST));

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    private ExpressionStatement parseExpressionStatement() {
        ExpressionStatement stmt = new ExpressionStatement(curToken);

        stmt.setExpression(parseExpression(LOWEST));

        if (peekTokenIs(TokenType.SEMICOLON)) {
            nextToken();
        }

        return stmt;
    }

    /**
     * Pratt Parsing 的核心：根據優先級解析表達式
     */
    private Expression parseExpression(int precedence) {
        Function<Void, Expression> prefix = prefixParseFns.get(curToken.getType());
        if (prefix == null) {
            noPrefixParseFnError(curToken.getType());
            return null;
        }
        Expression leftExp = prefix.apply(null);

        while (!peekTokenIs(TokenType.SEMICOLON) && precedence < peekPrecedence()) {
            Function<Expression, Expression> infix = infixParseFns.get(peekToken.getType());
            if (infix == null) {
                return leftExp;
            }

            nextToken();

            leftExp = infix.apply(leftExp);
        }

        return leftExp;
    }

    private Expression parseIdentifier() {
        return new Identifier(curToken, curToken.getLiteral());
    }

    private Expression parseIntegerLiteral() {
        try {
            long value = Long.parseLong(curToken.getLiteral());
            return new IntegerLiteral(curToken, value);
        } catch (NumberFormatException e) {
            String msg = String.format("could not parse %s as integer", curToken.getLiteral());
            errors.add(msg);
            return null;
        }
    }

    private Expression parsePrefixExpression() {
        PrefixExpression expression = new PrefixExpression(curToken, curToken.getLiteral());

        nextToken();

        expression.setRight(parseExpression(PREFIX));

        return expression;
    }

    private Expression parseInfixExpression(Expression left) {
        InfixExpression expression = new InfixExpression(curToken, left, curToken.getLiteral());

        int precedence = curPrecedence();
        nextToken();
        expression.setRight(parseExpression(precedence));

        return expression;
    }

    private Expression parseBoolean() {
        return new BooleanLiteral(curToken, curTokenIs(TokenType.TRUE));
    }

    private Expression parseGroupedExpression() {
        nextToken();

        Expression exp = parseExpression(LOWEST);

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }

        return exp;
    }

    private Expression parseIfExpression() {
        IfExpression expression = new IfExpression(curToken);

        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }

        nextToken();
        expression.setCondition(parseExpression(LOWEST));

        if (!expectPeek(TokenType.RPAREN)) {
            return null;
        }

        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }

        expression.setConsequence(parseBlockStatement());

        if (peekTokenIs(TokenType.ELSE)) {
            nextToken();

            if (!expectPeek(TokenType.LBRACE)) {
                return null;
            }

            expression.setAlternative(parseBlockStatement());
        }

        return expression;
    }

    private BlockStatement parseBlockStatement() {
        BlockStatement block = new BlockStatement(curToken);

        nextToken();

        while (!curTokenIs(TokenType.RBRACE) && !curTokenIs(TokenType.EOF)) {
            Statement stmt = parseStatement();
            if (stmt != null) {
                block.addStatement(stmt);
            }
            nextToken();
        }

        return block;
    }

    private Expression parseFunctionLiteral() {
        FunctionLiteral lit = new FunctionLiteral(curToken);

        if (!expectPeek(TokenType.LPAREN)) {
            return null;
        }

        parseFunctionParameters(lit);

        if (!expectPeek(TokenType.LBRACE)) {
            return null;
        }

        lit.setBody(parseBlockStatement());

        return lit;
    }

    private void parseFunctionParameters(FunctionLiteral lit) {
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken();
            return;
        }

        nextToken();

        Identifier ident = new Identifier(curToken, curToken.getLiteral());
        lit.addParameter(ident);

        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();
            nextToken();
            ident = new Identifier(curToken, curToken.getLiteral());
            lit.addParameter(ident);
        }

        if (!expectPeek(TokenType.RPAREN)) {
            return;
        }
    }

    private Expression parseCallExpression(Expression function) {
        CallExpression exp = new CallExpression(curToken, function);
        parseCallArguments(exp);
        return exp;
    }

    private void parseCallArguments(CallExpression exp) {
        if (peekTokenIs(TokenType.RPAREN)) {
            nextToken();
            return;
        }

        nextToken();
        exp.addArgument(parseExpression(LOWEST));

        while (peekTokenIs(TokenType.COMMA)) {
            nextToken();
            nextToken();
            exp.addArgument(parseExpression(LOWEST));
        }

        if (!expectPeek(TokenType.RPAREN)) {
            return;
        }
    }
}
