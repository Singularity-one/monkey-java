package com.monkey.ast;

import com.monkey.token.Token;

/**
 * ReturnStatement 代表 return 語句
 * 例如：return 5;
 */
public class ReturnStatement implements Statement {
    private final Token token; // TokenType.RETURN
    private Expression returnValue;

    public ReturnStatement(Token token) {
        this.token = token;
    }

    public void setReturnValue(Expression returnValue) {
        this.returnValue = returnValue;
    }

    public Expression getReturnValue() {
        return returnValue;
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
        StringBuilder out = new StringBuilder();
        out.append(tokenLiteral()).append(" ");

        if (returnValue != null) {
            out.append(returnValue.string());
        }

        out.append(";");
        return out.toString();
    }
}
