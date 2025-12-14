package com.monkey.ast;

import com.monkey.token.Token;

/**
 * PrefixExpression 代表前綴表達式
 * 例如：-5, !true
 */
public class PrefixExpression implements Expression {
    private final Token token; // 前綴運算符 token，例如 !
    private final String operator;
    private Expression right;

    public PrefixExpression(Token token, String operator) {
        this.token = token;
        this.operator = operator;
    }

    public void setRight(Expression right) {
        this.right = right;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
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
        return "(" + operator + right.string() + ")";
    }
}
