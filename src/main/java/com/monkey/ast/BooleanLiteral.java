package com.monkey.ast;

import com.monkey.token.Token;

/**
 * BooleanLiteral 代表布林字面值
 * 例如：true, false
 */
public class BooleanLiteral implements Expression {
    private final Token token; // TokenType.TRUE 或 TokenType.FALSE
    private final boolean value;

    public BooleanLiteral(Token token, boolean value) {
        this.token = token;
        this.value = value;
    }

    public boolean getValue() {
        return value;
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
        return token.getLiteral();
    }
}
