package com.monkey.ast;

import com.monkey.token.Token;

/**
 * Identifier 表示標識符
 * 例如: x, y, foobar
 */
public class Identifier implements Expression {
    private final Token token;   // IDENT token
    private final String value;  // 標識符名稱

    public Identifier(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public String getValue() {
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
        return value;
    }
}