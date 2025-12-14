package com.monkey.ast;

import com.monkey.token.Token;

/**
 * LetStatement 代表 let 語句
 * 例如：let x = 5;
 */
public class LetStatement implements Statement {
    private final Token token; // TokenType.LET
    private Identifier name;
    private Expression value;

    public LetStatement(Token token) {
        this.token = token;
    }

    public void setName(Identifier name) {
        this.name = name;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    public Identifier getName() {
        return name;
    }

    public Expression getValue() {
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
        StringBuilder out = new StringBuilder();
        out.append(tokenLiteral()).append(" ");
        out.append(name.string());
        out.append(" = ");

        if (value != null) {
            out.append(value.string());
        }

        out.append(";");
        return out.toString();
    }
}
