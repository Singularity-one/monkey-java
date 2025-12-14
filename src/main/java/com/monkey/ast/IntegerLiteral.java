package com.monkey.ast;
import com.monkey.token.Token;

/**
 * IntegerLiteral 代表整數字面值
 * 例如：5, 10, 12345
 */
public class IntegerLiteral implements Expression {
    private final Token token; // TokenType.INT
    private final long value;

    public IntegerLiteral(Token token, long value) {
        this.token = token;
        this.value = value;
    }

    public long getValue() {
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
