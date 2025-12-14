package com.monkey.ast;
import com.monkey.token.Token;

/**
 * Identifier 代表識別符號
 * 例如：let x = 5; 中的 x
 */
public class Identifier implements Expression {
    private final Token token; // TokenType.IDENT
    private final String value;

    public Identifier(Token token, String value) {
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
        return value;
    }
}
