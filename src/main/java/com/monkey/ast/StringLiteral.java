package com.monkey.ast;

import com.monkey.token.Token;

/**
 * StringLiteral 代表字串字面值
 * 例如："hello world"
 */
public class StringLiteral implements Expression {
    private final Token token; // TokenType.STRING
    private final String value;

    public StringLiteral(Token token, String value) {
        this.token = token;
        this.value = value;
    }

    public String getValue() {
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
        return "\"" + value + "\"";
    }
}
