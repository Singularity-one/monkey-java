package com.monkey.ast;

import com.monkey.token.Token;

/**
 * ExpressionStatement 代表表達式語句
 * 例如：x + 10;
 */
public class ExpressionStatement implements Statement {
    private final Token token; // 表達式的第一個 token
    private Expression expression;

    public ExpressionStatement(Token token) {
        this.token = token;
    }

    public void setExpression(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public Token getToken() {
        return token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String string() {
        if (expression != null) {
            return expression.string();
        }
        return "";
    }
}