package com.monkey.ast;

import com.monkey.token.Token;

import com.monkey.token.Token;

/**
 * BooleanLiteral 表示布爾字面量
 * 例如: true, false
 */
public class BooleanLiteral implements Expression {
    private final Token token;  // TRUE 或 FALSE token
    private final boolean value;

    public BooleanLiteral(Token token, boolean value) {
        this.token = token;
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public void expressionNode() {
    }

    @Override
    public Token getToken() {
        return token;
    }

    @Override
    public String tokenLiteral() {
        return token.getLiteral();
    }

    @Override
    public String string() {
        return token.getLiteral();
    }
}
